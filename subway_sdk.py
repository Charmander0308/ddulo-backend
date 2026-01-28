import json
import threading
import paho.mqtt.client as mqtt
import os
from dotenv import load_dotenv

# 124번에서 만든 Redis 모듈 (import 되는지 확인!)
from redis_client import RedisManager

load_dotenv()

class SubwayHelper:
    def __init__(self):
        # 1. Redis 연결 (124번 활용)
        self.redis = RedisManager().get_client()
        
        # 2. MQTT 설정
        self.mqtt_host = os.getenv("MQTT_HOST", "localhost")
        self.mqtt_port = int(os.getenv("MQTT_PORT", 8000))
        self.mqtt_topic = os.getenv("MQTT_TOPIC", "/platform")
        
        self.mqtt_data = {} 
        self.mqtt_client = mqtt.Client()
        self.mqtt_client.on_connect = self._on_connect
        self.mqtt_client.on_message = self._on_message

    def _on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            print(f"✅ [SDK] MQTT 연결 성공 (Topic: {self.mqtt_topic})")
            client.subscribe(self.mqtt_topic)
        else:
            print(f"❌ [SDK] MQTT 연결 실패 (Code: {rc})")

    # [핵심] 젯슨 JSON 데이터 파싱 로직
    def _on_message(self, client, userdata, msg):
        try:
            # 1. 젯슨이 보낸 문자열 디코딩
            payload_str = msg.payload.decode()
            payload_json = json.loads(payload_str)
            
            # 2. "data" 키 안에 있는 {'1-1': 2, ...} 꺼내기
            if "data" in payload_json:
                new_counts = payload_json["data"]
                self.mqtt_data.update(new_counts)
                # print(f"📡 데이터 수신: {new_counts}") # 디버깅용
                
        except json.JSONDecodeError:
            print(f"⚠️ JSON 형식이 아님: {msg.payload}")
        except Exception as e:
            print(f"⚠️ SDK 에러: {e}")

    def start_listening(self):
        """MQTT 수신 시작"""
        t = threading.Thread(target=self.mqtt_client.connect, args=(self.mqtt_host, self.mqtt_port, 60))
        t.start()
        self.mqtt_client.loop_start()

    def get_current_platform_data(self):
        return self.mqtt_data

    # --- 데이터 저장 (Schema 정의) ---
    
    # =================================================================
    # 🔥 [핵심] 전체 JSON을 통째로 조립해서 저장하는 함수
    # =================================================================
    def save_full_route_json(self, 
                             redis_key,           # 저장할 Key (예: route:222:748)
                             
                             # 1. 전체 요약 정보
                             total_time, 
                             estimated_time, 
                             probability,
                             
                             # 2. 출발역 정보 (메타 + 알고리즘 결과)
                             start_station_data,
                             
                             # 3. 환승역 정보 리스트 (메타 + 알고리즘 결과의 리스트)
                             transfer_stations_list,
                             
                             # 4. 도착역 정보 (메타만 있음)
                             end_station_data):
        """
        params 설명:
        - start_station_data: 딕셔너리. stationId, stationName... 그리고 'result' 안에 들어갈 congestion 데이터들 포함.
        - transfer_stations_list: 리스트. 각 요소는 환승역 하나의 정보 딕셔너리.
        - end_station_data: 딕셔너리. 도착역 정보.
        """

        # 1. Start Station 구조 조립
        start_station_payload = {
        "stationId": start_station_data["stationId"], # 꼭 채워줘야함!!!!!
            "stationName": start_station_data["stationName"],
            "lineName": start_station_data["lineName"],
            "direction": start_station_data["direction"], # 꼭 채워줘야함!!!!!
            "estimatedWatingSec": start_station_data["estimatedWatingSec"],
            "result": [
                {
                    # 알고리즘으로 구한 값들 (SDK 호출 시 넘겨줘야 함)
                    "carCongestions": start_station_data["carCongestions"],
                    "stationCongestions": start_station_data["stationCongestions"],
                    "totalCongestions": start_station_data["totalCongestions"],
                    "bestBoardings": start_station_data["bestBoardings"],
                    "comfortBoarding": start_station_data["comfortBoarding"],
                    "arrivalInfos": start_station_data["arrivalInfos"]
                }
            ]
        }

        # 2. Transfer Station 구조 조립 (리스트니까 반복문)
        transfer_station_payload = []
        for tf in transfer_stations_list:
            transfer_station_payload.append({
                "stationId": tf["stationId"],
                "stationName": tf["stationName"],
                "lineName": tf["lineName"],
                "direction": tf["direction"],
                "estimatedWatingSec": tf["estimatedWatingSec"],
                "results": [
                    {
                        "carCongestions": tf["carCongestions"],
                        "stationCongestions": tf["stationCongestions"],
                        "totalCongestions": tf["totalCongestions"],
                        "bestBoardings": tf["bestBoardings"],
                        "comfortBoarding": tf["comfortBoarding"],
                        "arrivalInfos": tf["arrivalInfos"]
                    }
                ]
            })

        # 3. End Station 구조 조립
        end_station_payload = {
            "stationId": end_station_data["stationId"],
            "stationName": end_station_data["stationName"],
            "lineName": end_station_data["lineName"],
            "NextStationName": end_station_data.get("NextStationName", None),
            "estimatedWatingSec": end_station_data.get("estimatedWatingSec", 0),
            "results": []
        }

        # 4. 최종 JSON 완성 (형이 준 구조 그대로)
        final_payload = {
            "totalTimeSecond": total_time,
            "estimatedBoardingTime": estimated_time, # 예: "2026-01-28T14:30:00"
            "boardingProbability": probability,
            
            "startStation": start_station_payload,
            "transferStation": transfer_station_payload,
            "endStation": end_station_payload
        }

        # 5. Redis 저장
        try:
            self.redis.setex(redis_key, 60, json.dumps(final_payload, ensure_ascii=False))
            print(f"🚀 [SDK] 전체 JSON 저장 완료: {redis_key}")
        except Exception as e:
            print(f"💥 [SDK] 저장 실패: {e}")

    # =================================================================
    # 🔥 [DataSet 2] 실시간 전광판용 (인접 열차 3개 + 탑승가능여부)
    # =================================================================
    def save_for_station_board(self, station_info, up_bound_list, down_bound_list):
        """
        :param station_info: { "stationId": 221, "stationName": "역삼역", "lineName": "2호선" }
        :param up_bound_list: 상행선 열차 정보 리스트 (최대 3개)
        :param down_bound_list: 하행선 열차 정보 리스트 (최대 3개)
        """
        
        # 1. 형님이 준 JSON 구조 그대로 조립
        payload = {
            "station": station_info,     # 역 정보 객체
            "upBound": up_bound_list,    # 상행선 리스트 (isBoardable 포함)
            "downBound": down_bound_list # 하행선 리스트 (isBoardable 포함)
        }
        
        # 2. Redis Key: realtime:역ID (예: realtime:221)
        # 역 ID는 station_info 딕셔너리에서 꺼내서 씁니다.
        try:
            station_id = station_info["stationId"]
            key = f"realtime:{station_id}"
            
            # 3. 저장 (TTL 120초 - 실시간이니까 짧게)
            self.redis.setex(key, 120, json.dumps(payload, ensure_ascii=False))
            print(f"🚀 [SDK] 실시간 전광판 데이터 저장 완료: {key}")
            
        except KeyError:
            print("💥 [SDK] 에러: station_info에 'stationId'가 없습니다.")
        except Exception as e:
            print(f"💥 [SDK] 저장 실패: {e}")

    def _save_to_redis(self, key, data, ttl):
        try:
            json_str = json.dumps(data, ensure_ascii=False)
            self.redis.setex(key, ttl, json_str)
            print(f"🚀 [SDK] 저장 완료: {key}")
        except Exception as e:
            print(f"💥 [SDK] 저장 실패: {e}")