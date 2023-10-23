## 차곡(Chagok) - [배포 링크](https://chagok.site/)
Chagok은 IT 공모전, 프로그래밍 스터디, 프로그래밍 프로젝트에 관한 데이터를 수집하여,<br>
Chagok 사이트에서 한번에 볼 수 있으며, 팀빌딩까지 할 수 있는 웹사이트 입니다.

## Chagok 프로젝트 백엔드 - 스크랩
스프링부트 Chagok 스크랩 프로젝트

### 프로젝트 소개
정보를 얻을 사이트에서 15분마다 감지하여 글에 관한 정보를 수집하고,<br>
데이터를 가공하여 데이터베이스에 저장합니다.

## 팀 구성

프론트엔드2, 백엔드2, 디자이너1

## 기술 스택
Framework<br>
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">

ORM Framework<br>
<img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">

데이터 수집<br>
<img src="https://img.shields.io/badge/Spring Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><br>
<img src="https://img.shields.io/badge/Spring Scheduler-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><br>
<img src="https://img.shields.io/badge/JSoup-007396?style=for-the-badge&logo=Java&logoColor=white">

Build Tool<br>
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">

CI/CD 구축 파이프라인<br>
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white">

서버환경<br>
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">

<br><br>
### 서버개요도
![Chagok Scraper 서버 개요도](./overview/images/스크랩서버%20개요도.png)

### 데이터 수집 프로세스
![데이터 수집 프로세스](./overview/images/데이터%20수집%20프로세스.png)


## 데이터 수집 스케마

#### IT공모전(해커톤)
* 제목
* 모집 시작일
* 모집 종료일
* 주최 기관
* 본문
* 이미지 Url

#### 프로젝트/스터디
* 제목
* 개시일
* 본문
* 사이트 유형
* 기술 태그 리스트
* 프로젝트 및 스터디의 카테고리 유형


## 순서도
![Chagok Scraper 순서도](./overview/images/scraper%20순서도.png)
