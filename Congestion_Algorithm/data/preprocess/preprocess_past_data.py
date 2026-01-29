import pandas as pd
import numpy as np
import ast

PLATFORM_AREA = 30.24
CAR_CAPACITY = 160
STD_DENSITY = 4.3

print("[1] Loading Data...")
try:
    base_path = 'Congestion_Algorithm/data/'
    car_data = pd.read_csv(base_path + 'tmap_puzzle_filtering_car.csv', encoding='utf-8')
    get_off_data = pd.read_csv(base_path + 'tmap_puzzle_filtering_get-off.csv', encoding='utf-8')
    print(f" -> Done (Car: {car_data.shape}, Get-off: {get_off_data.shape})")
except Exception as e:
    print(f"[Error] {e}")
    exit()

def safe_literal_eval(val):
    if isinstance(val, str):
        try:
            return ast.literal_eval(val)
        except (ValueError, SyntaxError):
            return []
    return val

if 'congestionCar' in car_data.columns:
    car_data['congestionCar'] = car_data['congestionCar'].apply(safe_literal_eval)

if 'getOffCarRate' in get_off_data.columns:
    get_off_data['getOffCarRate'] = get_off_data['getOffCarRate'].apply(safe_literal_eval)

def calculate_average_by_group(df, value_col):
    group_cols = [
        'subwayLine', 'stationName', 'stationCode', 'updnLine', 
        'dow', 'hh', 'mm', 'prevStationName', 'prevStationCode'
    ]
    existing_cols = [c for c in group_cols if c in df.columns]

    if not existing_cols:
        return pd.DataFrame()

    def list_mean(series):
        matrix = np.array(series.tolist())
        if matrix.ndim == 1: 
            return matrix.tolist()
        return np.mean(matrix, axis=0).tolist()

    return df.groupby(existing_cols)[value_col].apply(list_mean).reset_index()

print("[2] Processing Groups...")
df_car_avg = calculate_average_by_group(car_data, 'congestionCar') if 'congestionCar' in car_data.columns else pd.DataFrame()
df_getoff_avg = calculate_average_by_group(get_off_data, 'getOffCarRate') if 'getOffCarRate' in get_off_data.columns else pd.DataFrame()

merge_keys = [c for c in df_car_avg.columns if c in df_getoff_avg.columns and c != 'congestionCar' and c != 'getOffCarRate']

df_merged = pd.merge(df_car_avg, df_getoff_avg, on=merge_keys, how='inner')

print("[3] Matching Next Station...")
df_next_lookup = df_merged[['dow', 'hh', 'mm', 'updnLine', 'prevStationCode', 'congestionCar']].copy()
df_next_lookup.rename(columns={'congestionCar': 'next_station_congestion'}, inplace=True)

df_final = pd.merge(
    df_merged,
    df_next_lookup,
    left_on=['dow', 'hh', 'mm', 'updnLine', 'stationCode'],
    right_on=['dow', 'hh', 'mm', 'updnLine', 'prevStationCode'],
    how='left',
    suffixes=('', '_next_dup')
)
df_final = df_final.loc[:, ~df_final.columns.str.endswith('_next_dup')]
# df_final = df_final.dropna(subset=['next_station_congestion'])

print("[4] Calculating Congestion...")
df_final['totalCongestion'] = df_final['next_station_congestion'].apply(lambda x: x if isinstance(x, list) else [])

def calc_platform_congestion(row):
    if not isinstance(row['next_station_congestion'], list) or not row['congestionCar']:
        return [], []

    curr = np.array(row['congestionCar'])
    next_s = np.array(row['next_station_congestion'])
    off_rate = np.array(row['getOffCarRate'])

    if len(curr) != len(next_s):
        return [], []

    rem_ratio = curr * (1 - (off_rate / 100.0))
    boarding = np.maximum(next_s - rem_ratio, 0)
    people = boarding * (CAR_CAPACITY / 100.0)
    congestion = (people / PLATFORM_AREA / STD_DENSITY) * 100

    return np.round(people, 2).tolist(), np.round(congestion, 2).tolist()

df_final[['platformPeople', 'platformCongestion']] = df_final.apply(calc_platform_congestion, axis=1, result_type='expand')


output_path = base_path + 'past_average_congestion.csv'
df_final.to_csv(output_path, index=False, encoding='utf-8-sig')

print(f"[Complete] Saved to {output_path}")
print(df_final[['stationCode', 'congestionCar', 'platformCongestion', 'totalCongestion']].head())