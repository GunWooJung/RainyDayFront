# 🏠 레이니데이 

📍 **이동 경로 기반 날씨 조회 서비스**

기존의 주소 기반 이동 경로 관리 서비스는 많지만, 사용자는 별도로 날씨를 조회해야 하는 번거로움이 있다. 이 서비스는 사용자의 이동 경로에 맞춰 해당 위치와 시간대의 날씨를 실시간으로 제공하여 편리한 일정 관리를 돕는다.

## 🎯 담당 역할

- **안드로이드 앱 개발**: 기상청 OpenAPI 조회, 네이버지도 길 찾기를 제외한 모든 안드로이드 앱 개발 담당

---

## 📄 기술 스택

- **클라이언트 개발**: Java, Android Stdio
- **백엔드**: Spring Boot, MySQL, AWS
- **API**: 기상청 OpenAPI, 네이버 지도 API, Google Directio API
---

## ✨ 주요 기능

✅ **로그인** – 회원 가입하고 로그인을 할 수 있습니다.
✅ **현재 위치 날씨** – 핸드폰 GPS를 통해 현재 위치의 날씨를 1시간 간격으로 3일 후까지 볼 수 있습니다.
✅ **일정 기반 날씨** – 출발지, 경유지, 목적지를 검색하여 찾고, 시간대를 설정하여 일정을 등록하면 해당 장소와 시간대의 날씨를 볼 수 있습니다.
✅ **알림** – 출발 시간에 맞게 PUSH 알림을 설정할 수 있습니다.

---

## 🏗️ 시스템 아키텍처

![시스템 아키텍처](https://github.com/GunWooJung/READMEImage/blob/main/30ticket.JPG)

---

## 📺 시연 영상 및 문서

📌 **시연 영상**: [YouTube 링크](https://youtu.be/A51GNjRFSCw?si=Jbqzho_C7b90Wy97)
📌 **E-R 다이어그램**:

![ERD](https://github.com/GunWooJung/READMEImage/blob/main/erd.png)
