# Layered Architecture using DDD / Hexagonal and Spring, grpc-kotlin

해당 프로젝트의 내부 도메인 레이어(inside)는 외부 레이어(outside)와 관련없이 내부 비즈니스 모델에 집중하며 외부의 연동을 허용하기 위하여 포트(interface)를 사용합니다.<br/><br/>
이는 일반적으로 알려진 DDD Layered Architecture, Hexagonal 의 개념을 차용 하였으며, 클라이언트의 요청과 외부 API 연동과 DB 엑세스를 위해 Application(outside) / Domain(inside) / Infrastructure(outside) 로 나누어진 레이어 구조를 가집니다.

![ddd.png](public%2Fimages%2Fddd.png)

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

- boot
  - 프로젝트의 진입점(실행), application yaml 설정 관리
  - 모든 모듈을 참조
- api **(outbound)**
  - Layered Architecture의 application layer 개념
  - client의 요청을 받는 레이어로 gRPC 요청을 받는 grpcService 포함
  - domain을 참조
  - proto 디렉토리 - [proto directory](api%2Fsrc%2Fmain%2Fproto%2Forg%2Fmango%2Fdata)
- domain **(inbound)**
  - 모델을 구성하고 모델에 대한 비즈니스 로직에만 집중
  - infrastructure에 정의된 객체들을 호출하지만 직접 호출하지 않고 domain 에 정의된 interface 기반 의존성 주입
- infrastructure **(outbound)**
  - 외부 서비스 접근을 담당하며, DB Access 를 수행
  - domain을 참조

## Test

----

**domain 모듈에서 유닛 테스트**, **infrastructure 모듈에서는 통합 테스트**를 수행하였으며, 통합테스트를 수행하기 위해 testcontainer를 적용하였습니다.

- domain - [domain layer test directory](domain%2Fsrc%2Ftest%2Fkotlin%2Forg%2Fmango%2Fdata%2Fservice)
- infrastructure - [infrastructure layer test directory](infrastructure%2Fsrc%2Ftest%2Fkotlin%2Forg%2Fmango%2Fdata%2Frepository)

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

## Feature

----

주문 데이터를 관리하는 서버입니다. 아래 proto 파일 명세대로 구현 하였으며, 주문 관련하여 아래와 같은 기능을 제공합니다.

- 주문 등록
- 상품 추가
- 상품 제거
- 주문 완료처리

[order.proto](api%2Fsrc%2Fmain%2Fproto%2Forg%2Fmango%2Fdata%2Forder.proto)
```protobuf
syntax = "proto3";

package org.mango.data;

option java_package = "org.mango.data";
option java_multiple_files = true;

import "google/protobuf/empty.proto";

service OrderService {
  rpc CreateOrder(OrderItemRequest) returns (CreateOrderResponse);
  rpc AddOrderItem(AddOrderItemRequest) returns (google.protobuf.Empty);
  rpc CompleteOrder(CompleteOrderRequest) returns (google.protobuf.Empty);
  rpc DeleteOrderItem(DeleteOrderItemRequest) returns (google.protobuf.Empty);
}

message OrderItemRequest {
  double price = 1;
}

message CreateOrderResponse {
  string id = 1;
}

message AddOrderItemRequest {
  string id = 1;
  OrderItemRequest orderItem = 2;
}

message CompleteOrderRequest {
  string id = 1;
}

message DeleteOrderItemRequest {
  string id = 1;
  string orderItemId = 2;
}
```

## References

----
- [Organizing Layers Using Hexagonal Architecture, DDD, and Spring](https://www.baeldung.com/hexagonal-architecture-ddd-spring)
- [Gradle과 함께하는 Backend Layered Architecture](https://medium.com/riiid-teamblog-kr/gradle%EA%B3%BC-%ED%95%A8%EA%BB%98%ED%95%98%EB%8A%94-backend-layered-architecture-97117b344ba8)
- [Hexagonal architecture](https://alistair.cockburn.us/hexagonal-architecture/)