## Initialize Repository

Initialize Repository(레포지터리 초기화) 버튼을 누르면 .git 폴더 생성 (우상단 바 3개 모양 탭 클릭 후 show hidden files 클릭하면 보임)


## Git - VScode 연결

아래 명령어를 vscode terminal 창에 입력하여 git과 local repository 연결

```bash
$ git config --global user.name '{user_name}'
$ git config --global user.email '{user_email}'
```

add remote를 누른 뒤 repository의 url을 입력하면 git repository에 연결됨. remote name은 **origin**이라 할 것(관행).

python 파일 옆에 U가 있으면 Git에 업로드 되지 않았다는 의미. 왼쪽에 source control에서 변경한 파일에 커서를 가져가면 + 표시가 뜸. +를 누르면 Index에 추가됨


## VScode에서 파일 수정 후 commit, push


파일을 수정하고 저장하면 source control 창에서 M이 뜨는 것을 확인할 수 있음.

다시 [+] 아이콘을 눌러 staging을 한 후 comment를 작성하고 commit을 누르면 commit이 된다.

그리고 우상단에 점 3개를 누르고 push를 하면 정상적으로 push가 된다.

깃에는 3가지 저장공간이 있다.

- Working Directory : 유저가 파일을 수정하는 작업공간을 말한다. visual studio에서 연 폴더라고 생각하면 된다.
- Stage Area(.git의 index파일) : 유저가 만들고 수정한 작업 중 Repoitory(git)에 올릴 작업을 의미한다. 유저가 파일을 수정했더라도 Stage Area에 올리지 않으면 커밋되지 않는다.
- Repository(.git) : 버전관리를 위한 저장소이다. 우리가 흔히 생각하는 git을 의미한다.

*Staged Changes에 파일이 없고  Changes에만 파일이 있는 채 commit하게 되면 changes에 있는 모든 파일이 commit 된다.

## commit log 확인
```
git log --oneline --graph --all
```

## branch 추가

```
git checkout -b name
```

## git pull

```python
# 원하는 폴더에 git repository 가져오기
cd 어쩌구
git clone 주소
```

```python
# copy
cp -r 대상 원하는곳
```

```python
# git clone 한 곳 들어가서 변경사항 확인
git status

# 전체 추가
git add .
# 일부 추가
git add /pcdet
git status

#commit
git commit -m "설명"

# push
git push
```

```python
git chechout name #브랜치 변경
git branch #브랜치 확인
git status # 변경한 내용 확인
git add [file] or [folder] # 추가할 부분
git status
git commit -m "설명"
git push --set-upstream origin name **OR** git push origin name
```

---

# git 설치

```bash
apt-get install git
```

# 사용자 인증정보 설정

로컬 저장소에서 원격 저장소로 데이터를 넘길때 commit한 사람이 누구며, 어떠한 사람인지 남기기 위해 PC의 git 시스템 환경설정정보

```bash
git config --global user.name '{user_name}'
git config --global user.email '{user_email}'
```

- `user.name` : 시스템 이름
- `user.email` : 이메일

# 원격 repo와 로컬 repo 연결하기

하나의 로컬 repo에서 여러개의 원격 repo를 연결할 수도 있다.

## 원격 repo → 로컬 repo 연결

```bash
git clone [URL]
```

## 로컬 repo → 원격 repo 연결

```bash
git init
git remote add origin [URL]
```

- origin : 연결할 원격 repo의 이름 (마음대로 설정 가능)
- `git remote -v` : 연결된 원격 repo의 이름과 url 확인가능

# Branch

일반적으로 기능별로 브랜치 생성

```bash
# 브랜치 생성
git checkout -b [브랜치 이름]
# 브랜치 변경
git checkout [브랜치 이름]
```

## git flow vs github flow

- 일반적으로 회사에서는 git flow 중심의 브랜치 전략을 취한다고 들었음
- 모든 깃 사용 전략은 팀내에서 정하면 됨


Git flow : local 중심 branch 전략

`main` (=master), `develop`, `release`, `feature`, `hotfix` 등의 브랜치 사용

feature에서 작업 후 develop에 merge

develop에서 다음 릴리즈를 위한 개발이 끝나면 release에 merge

release에서 bugfix, refactoring을 진행하고 끝나면 main에 merge

GitHub flow : remote 중심 branch 전략

`main` (main+develop+release), `feature` (feature+hotfix) 등의 브랜치 사용

remote(원격)에 작업한 내용을 수시로 push하여 remote를 항상 최신 상태로 유지

배포를 위한 브랜치가 따로 없기 때문에 main 브랜치에 merge는 항상 주의해야함

# Issue & Pull Request

### Issue

해야할 task에 맞춰서 issue 생성, = 개발 작업 단위

작업의 history 관리를 위해 작성 추천

- development에 해당 task와 관련된 브랜치 추가
- 해당하는 projects 추가
- 해당 Task를 수행하는 팀원 assignees 추가

### PR

다른 브랜치로 merge 작업을 진행하기 전 팀원에게 코드리뷰를 받는 단계

- projects는 추가 잘 안함
- 관련 issue추가
- 코드 리뷰할 팀원 reviewers 추가

코드 리뷰 후 Merge 방법

- Create a merge commit : Merge 커밋을 추가로 남기고 Merge
- Squash and merge : 커밋 로그들을 하나로 합치면서 Merge
- Rebase and Merge : Merge 커밋을 남기지 않으면서 기존 커밋 로그를 유지하는 상태로 Merge
    
    별다른 뜻이 없다면 Create a merge commit을 사용하면 됨
    
    merge 후 자동으로 이슈 닫힘
    


- 이런식으로 코드리뷰 남기는게 중요!!!
- 보통 회사에서 포트폴리오로 git을 많이 봄
- 얼만큼 잘 활용하고 있는지, 어떤 식으로 task를 수행하고 있는지, 회의한 내용들을 최대한 이슈나 pr에 남기도록…

### issue&Pull Request template

팀내에서 이슈 작성 템플릿 정하는 것을 추천

`.github`폴더 내의 다음과 같은 .md파일 추가


- ISSUE_TEMPLATE.md
    
    ```bash
    ⭐ Description
    ---
    -
    
    📷 Screenshots
    ---
    -
    
    📁 Files
    ---
    -
    
    📈 To Reproduce
    ---
    -
    
    ✔️ Tasks
    ---
    - [ ] Task 1
    - [ ] Task 2
    - [ ] Task 3
    
    ```
    
- PULL_REQUREST_TEMPLATE.md
    
    ```bash
    📌 개요
    ---
    -
    
    💻 작업사항
    ---
    -
    
    ✅ 변경로직
    ---
    -
    
    💡Issue 번호
    ---
    - [ ]
    
    ```
    

# add, commit, push

코드 수정 후 원격에 코드를 올릴 때 사용

```bash
git add [추가할 파일 / .] # .은 수정된 전체 파일을 올리는 것
git commit -m "feat : 수정한 내용 요약
- 설명 # 추가적으로 설명이 필요할때
- #1" # 이슈번호 달기
git push origin [branch name]
```

add 취소

```bash
git reset [파일명]
```

가장 최근 commit 메세지 변경

```bash
git commit --amend -m ""
```

commit 취소

- 변경 내용을 취소하는 새 커밋을 생성
- 원격 저장소로 푸쉬된 경우, 커밋 기록을 덮어쓰지 않는 revert 추천

```bash
git log # 커밋 이름 확인
git revert [커밋 이름]
```

- 커밋 기록을 덮고 싶을때
    - `--hard`  : 변경사항이 모두 제거된다 (이전 상태로 돌아가면서 수정됐던 코드가 다 사라지니 주의하자)
    - `--soft` : 커밋되지 않은 변경사항이 손실되지 않음

```bash
git reset --hard [커밋 이름]

# --hard를 써야할때 변경사항을 따로 저장하는 법
git stash # add 이후 commit 이전의 데이터 모두 임시저장
git reset --hard [커밋 이름]
git pop # 후입선출 방식, 다시 불러오기
```

push 취소

- `reset` 사용 (위와 같음)

# pull

= fetch + merge

원격에 수정된 코드를 받아올 때 사용

로컬에는 없지만 원격에 있는 내용들을 로컬 repo에 저장하고 merge

작업을 시작하기 전, 다른 팀원이 코드를 수정했을 때, PR을 완료했을 때 항상 pull을 한다.

(충돌을 피하기 위해 습관적으로 pull을 받자….)

- conflict 났을 때

```bash
git add .
git stash
git pull
```

# 참고

**(추가로 알면 좋은 것들)**

### **reject과 conflict는 완전 다름**

- Reject은 원격저장소의 내용을 갖고있지 않은채로 push하려고할때

### **3 way merge**

- rebase
- revert
- cherry-pick

### **git 동작 원리**


- 왼쪽과 같은 형태로 브랜치/파일에 접근한다
- checkout : HEAD가 가리키는 것을 바꿈
- reset : 브랜치가 가리키는 상태를 바꿈

### **pre-commit**

- 커밋할 때, 정해진 스크립트들을 실행할 수 있게 해주는 툴
    - 코드를 정리해주는 black, flask8나 unit test, pytest 등 등록가능