💻 helpMe 소개
your_gif_file.gif

helpMe 는

Preview
앱 캡쳐이미지

간단 글 작성

👫🏻팀원
👴🏻 이현호 (HYU CSE 21)

👧🏻 오세연 (KAIST SoC 22)

Untitled

KakaoTalk_Photo_2024-07-10-17-15-26.jpeg

개발환경
FrontEnd

OS: Android
IDE: Android Studio
Language: Kotlin
BackEnd

IDE: NodeJs
DB: MySQL
Language: JavaScript
📱서비스 소개
1️⃣ 로그인
(로그인창 화면)

주요 기능

카카오톡 계정을 통한 회원가입, 로그인
기술 설명

서버에 POST request로 이메일, 이름을 전송하여 DB의 user_info table에 저장됩니다.
2️⃣ 내 학습내역
(학습내역 페이지)

주요 기능

내 프로필을 표시합니다
이름
카카오톡 프로필 이미지
새로운 Project를 생성할 수 있습니다.
현재 사용자가 학습중인 프로젝트들을 표시합니다.
Project 이름
시작 날짜
끝 날짜
학습중인 language
project type
기술 설명

recyclerview를 사용하여 학습 중인 모든 목록을 확인합니다.
POST request를 통해 새로운 project에 대한 정보를 DB에 저장합니다. proj table의 primary key인 proj_id는 auto increase constraint를 적용해 proj_title이 삽입되는 순간 자동으로 생성되도록 합니다.
GET request를 통해 user_id가 내 id와 일치하는 project의 데이터를 받아옵니다.
3️⃣ 학습 상세페이지
(상세페이지)

주요기능

하나의 Project에 대해 상세한 내용들을 기록합니다.
Contents: 학습한 내용들을 글로 기록합니다.
Reference: 학습에 참고가 된 자료들을 기록합니다.
Remember: 학습을 하며 느낀점 등을 기록합니다.
내가 작성한 project의 경우 내용을 등록/수정할 수 있습니다.
남이 작성한 Projectd의 경우 “좋아요”버튼을 누를 수 있습니다.
기술 설명

고유값인 사용자 이메일을 Intenet로부터 수신하여 프로젝트의 작성자와 일치할 때는 프로젝트에 좋아요 버튼을 생성하지 않고, 다를 때만 좋아요 상호작용 버튼을 생성하는 함수를 통해 해당 Activity의 레이아웃을 결정합니다.
content, reference, remember을 GET, POST request를 통해 DB에 저장, 수정합니다.
isLiked의 boolean값에 따라 proj table의 likes 값을 증가, 또는 감소시킵니다.
4️⃣ 학습 둘러보기
(둘러보기 동영상)

주요 기능

다른 사람들의 학습 기록을 둘러볼 수 있습니다.
검색 키워드(language, project type)에 따라 검색 결과를 불러옵니다.
좋아요를 많이 받은 순서대로 나열합니다.
기술 설명

projects.filter 를 사용해서 조건에 맞는 프로젝트만 선별합니다.
private fun filterProjects() {
        if (!::projects.isInitialized) {
            projects = mutableListOf()
        }
        val filteredProjects = projects.filter { project ->
            (selectedLanguage == null || project.lan?.equals(selectedLanguage, ignoreCase = true) == true) &&
                    (selectedType == null || project.type?.replace(" ", "")?.equals(selectedType?.replace(" ", ""), ignoreCase = true) == true)
        }
        adapter.updateProjects(filteredProjects)
    }
proj table에 존재하는 모든 project에 대한 결과를 GET request를 통해 가져옵니다. ORDER BY LIKES constraint를 통해 받은 좋아요 개수에 따라 data 를 select합니다.
5️⃣ My Page
(myPage 사진)

주요기능

내 프로필 및 Status를 보여줍니다
진행중인 Project의 수
완료된 Project의 수
내가 받은 좋아요 수의 총 합
가장 많이 학습한 language
가장 많이 학습한 project type
기술 설명

GROUP BY constraint를 이용해 최빈값을 SELECT 했습니다.
DB
Untitled

ApiService

Design
Untitled
