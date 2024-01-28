# Layered Architecture using Hexagonal, DDD, Spring, kotlin and gRPC

Key / Value 데이터를 관리하는 서버입니다. 아래 proto 파일 명세대로 구현 하였으며, 상세조회 / 저장 기능을 담당합니다.

[data.proto](api%2Fsrc%2Fmain%2Fproto%2Forg%2Fmango%2Fdata%2Fdata.proto)
```protobuf
syntax = "proto3";

package org.mango.data;

option java_package = "org.mango.data";
option java_multiple_files = true;

import "google/protobuf/empty.proto";

service KeyValueService {
  rpc Get(GetRequest) returns (GetResponse);
  rpc Save(SaveRequest) returns (SaveResponse);
}

message GetRequest {
  string key = 1;
}

message GetResponse {
  string key = 1;
  oneof result {
    string value = 2;

    google.protobuf.Empty no_value = 3;
  }
}

message SaveRequest {
  string key = 1;
  string value = 2;
}

message SaveResponse {
  string key = 1;
  string value = 2;
}
```

## Requirements

----

|                | Main version |
|----------------|--------------|
| Java           | ^21          |
| Kotlin         | ^1.9.22      |
| Docker         | ^23.0.5      |
| Docker-Compose | ^2.17.3      |

## Getting Started

----

**서버 실행**
```bash
# init 디렉토리 이동
$ cd init

# MySQL container 설치 / 실행
$ docker compose -f docker-compose.yaml -p init up -d

# root 경로 이동
$ cd ..

# 프로젝트 빌드
$ ./gradlew build

# Spring Boot 실행
$ ./gradlew :boot:bootRun
```

## Structure

----

멀티모듈 구조로 레이어 단위로 나뉘어져 있으며 아래 이미지와 같은 구조와 각 프로젝트별 연관 관계를 가지고 있습니다.

![ddd.png](public%2Fimages%2Fddd.png)
- boot
  - 프로젝트의 진입점(실행), application yaml 설정 관리
  - 모든 모듈을 참조
- api **(outbound)**
  - Layered Architecture의 application layer 개념
  - client의 요청을 받는 레이어로 gRPC 요청을 받는 grpcService 포함
  - domain을 참조
  - proto 파일 - [data.proto](api%2Fsrc%2Fmain%2Fproto%2Forg%2Fmango%2Fdata%2Fdata.proto)
- domain **(inbound)**
  - 모델을 구성하고 모델에 대한 비즈니스 로직에만 집중
  - infrastructure에 정의된 객체들을 호출하지만 직접 호출하지 않고 domain 에 정의된 interface 기반 의존성 주입
- infrastructure **(outbound)**
  - 외부 서비스 접근을 담당하며, DB Access 를 수행
  - domain을 참조

## Test

----

**domain 모듈에서 유닛 테스트**, **infrastructure 모듈에서는 통합 테스트**를 수행하였으며, 통합테스트를 수행하기 위해 testcontainer를 적용하였습니다.

- domain - [KeyValueInputServiceTest.kt](domain%2Fsrc%2Ftest%2Fkotlin%2Forg%2Fmango%2Fdata%2Fservice%2FKeyValueInputServiceTest.kt)
- infrastructure - [MySQLValueRepositoryTest.kt](infrastructure%2Fsrc%2Ftest%2Fkotlin%2Forg%2Fmango%2Fdata%2Frepository%2FMySQLValueRepositoryTest.kt)

**전체 테스트 수행**
```bash
# 모듈 전체 테스트 수행
$ ./gradlew test
```

**모듈별 테스트 수행**
```bash
# domain 모듈 테스트 수행
$ ./gradlew :domain:test

# infrastructure 모듈 테스트 수행
$ ./gradlew :infrastructure:test
```

## Conclusions

----

kotlin-grpc, spring boot webflux, r2dbc 를 사용하여 개발 하였습니다<br/>

병렬로 여러 요청을 처리하기 위해 호출 함수들은 suspend function 으로 정의되어 있으며, I/O 요청 작업은 withContext 내에서 수행되고 DB 연동은 R2DBC를 활용하여 구현하였고 이외에 나머지 필수 요구사항, 선택사항을 모두 적용했습니다.<br/>

혹 궁금하신 점들이나 실행이 안되는 이슈가 발생한다면 말씀 주시면 답변 드리도록 하겠습니다! 감사합니다 :)