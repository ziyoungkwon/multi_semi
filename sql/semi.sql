create database multi_semi_project;

use multi_semi_project;


#------------------ 관광지 테이블

drop table place;
drop table rev;
drop table fav;

create table if not exists place(
                                    no bigint primary key auto_increment,
                                    dist int not null,
                                    title varchar(100) not null,
                                    descr text null,
                                    addr varchar(255) not null,
                                    phone varchar(100) null,
                                    lat decimal(15, 10) not null,
                                    lng decimal(15, 10) not null,
                                    content_id int not null,
                                    first_img varchar(500) null
);

select * from place;


update place set descr = '1974년 10월 3일 개장하여 한국의 전통문화와 민속적인 삶을 재현하고 있는 곳으로 이를 통해 조상들의 지혜와 생활 모습을 느낄 수 있는 곳이다. 휴일 없이 365일 개장한다. 당연히 정부 산하기관이나 공기업 산하 시설이라 생각하겠지만 실제로는 민간기업이 소유해서 운영 중이다. 현재 전통 기와집과 초가집을 비롯해 관가, 반가(班家), 주막 등이 있고 이외에도 양반댁, 도기(陶器) 가마 터, 유기 공방, 서당, 약방, 관상소 등 조선시대 때 존속했던 건물들을 재현·보존하고 있다. 각종 민속 음식과 민속 장터 등이 있으며 이외에 놀이동산과 민속 박물관 등이 있고 방송사 사극 드라마 촬영 장소로 자주 활용되었다. 자체 세트장이 있어도 일부 장면은 여기서 촬영하곤 하는데, 특히 1970~80년대는 VFX기술이 아예 없었고 사극 옥외 세트장이라는 것이 궁궐이나 일부 사찰[6]을 빼면 전무했기 때문에 조선왕조 오백년 등 사극에서는 단골 촬영지였다. 각 방송사들이 문경 등지에 오픈세트를 지어놓은 지금도 자주 활용되고 있다. 왜냐하면 한국민속촌은 촬영 사용료가 무료이기 때문. 바로 이것이 다른 장소협조지와 달리 사극 중간에 "장소협조 한국민속촌"이라는 문구가 나오는 이유이기도 하다. 다만 요즘은 한국민속촌에서 찍더라도 예전처럼 중간에 장소 협찬 자막이 나오지 않고 엔딩 크레딧에 나온다.'
where no = 21;

#--------------------- 지역 코드 테이블

create table if not exists area_code(
                                        no bigint primary key auto_increment,
                                        code int not null unique,
                                        name varchar(20) not null unique
);

INSERT INTO area_code (code, name) VALUES
                                       (1, '서울'),
                                       (2, '인천'),
                                       (3, '대전'),
                                       (4, '대구'),
                                       (5, '광주'),
                                       (6, '부산'),
                                       (7, '울산'),
                                       (8, '세종특별자치시'),
                                       (31, '경기도'),
                                       (32, '강원특별자치도'),
                                       (33, '충청북도'),
                                       (34, '충청남도'),
                                       (35, '경상북도'),
                                       (36, '경상남도'),
                                       (37, '전북특별자치도'),
                                       (38, '전라남도'),
                                       (39, '제주도');

select * from area_code;


#--------------------- 관광지 이미지 테이블


create table if not exists place_img(
                                        no bigint primary key auto_increment,
                                        title varchar(100) not null,
                                        org_url varchar(500) not null
);


select * from place_img;

#---------------------  멤버 테이블

create table if not exists mem(
                                  no bigint primary key auto_increment,
                                  id varchar(20) unique not null,
                                  email varchar(50) unique not null,
                                  pwd varchar(500) not null,
                                  name varchar(50) not null,
                                  phone varchar(100),
                                  addr varchar(255),
                                  intro text,
                                  role varchar(100) default 'ROLE_USER'
);


# --------------------- 리뷰 테이블

create table if not exists rev(
                                  no bigint primary key auto_increment,
                                  title varchar(100) not null,
                                  content text not null,
                                  rate int default 0 check (rate >= 0 and rate <= 5),
                                  writer_no bigint not null,
                                  place_no bigint not null,
                                  img_url varchar(500),
                                  created_at datetime default current_timestamp not null,
                                  modified_at datetime default current_timestamp on update current_timestamp not null, -- 수정일 자동 갱신 설정 추가
                                  modified_by bigint not null,
                                  foreign key (writer_no) references mem(no) on delete cascade,
                                  foreign key (place_no) references place(no) on delete cascade,
                                  foreign key (modified_by) references mem(no) on delete cascade

);

select * from rev;


#--------------------- AI이미지 테이블

create table if not exists ai_img(
                                     no bigint primary key auto_increment,
                                     place_img_no bigint not null,
                                     mem_no bigint not null,
                                     org_url varchar(500) not null,
                                     thum_url varchar(500) not null,
                                     created_at datetime default current_timestamp not null,
                                     foreign key (place_img_no) references place_img(no) on delete cascade,
                                     foreign key (mem_no) references mem(no) on delete cascade
);


select * from ai_img;


#--------------------- 즐겨찾기 테이블

create table if not exists fav(
                                  no bigint primary key auto_increment,
                                  mem_no bigint not null,
                                  place_no bigint not null,
                                  foreign key (mem_no) references mem(no) on delete cascade,
                                  foreign key (place_no) references place(no) on delete cascade
);

select * from fav;

#---------------------- 리프레시토큰 테이블


create table if not exists ref_token(
                                        no bigint primary key auto_increment,
                                        email varchar(50) not null unique,
                                        token varchar(500) not null unique,
                                        created_at datetime default current_timestamp not null,
                                        expired_at datetime default current_timestamp on update current_timestamp not null,
                                        foreign key (email) references mem(email) on delete cascade on update cascade
);

select * from ref_token;

insert into mem (id, email, pwd, name, phone, addr, intro) values (1234, 'gy011003@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);
insert into mem (id, email, pwd, name, phone, addr, intro) values ('test1', '111@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);
insert into mem (id, email, pwd, name, phone, addr, intro) values ('test2', '222@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);
insert into mem (id, email, pwd, name, phone, addr, intro) values ('test3', '333@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);
insert into mem (id, email, pwd, name, phone, addr, intro) values ('test4', '444@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);
insert into mem (id, email, pwd, name, phone, addr, intro) values ('test5', '555@gmail.com', '$2a$12$lu6975A9H8wsgRhZL.uk..sOfcfwRAh1PM4Y2A6nbWlhvNuE/u6B.', 1234, 1234, 1234, 1234);

INSERT INTO rev (title, content, rate, writer_no, place_no, img_url, modified_by)
VALUES
    ('맛집 인정', '음식이 정말 맛있었어요! 재방문 의사 100%', 5, 4, 1, 'https://example.com/img/review1.jpg', 4),
    ('조용한 분위기', '카페 분위기가 너무 조용해서 공부하기 좋았어요.', 4, 7, 1, 'https://example.com/img/review2.jpg', 7),
    ('가격 대비 아쉬움', '맛은 괜찮았지만 가격이 조금 비쌌어요.', 3, 5, 2, NULL, 5),
    ('서비스 굿', '직원분들이 친절해서 기분 좋게 식사했습니다.', 5, 6, 3, 'https://example.com/img/review3.jpg', 6),
    ('사진이랑 다름', '사진 보고 기대했는데 실물은 좀 달랐어요.', 2, 5, 4, 'https://example.com/img/review4.jpg', 5);

insert into rev (title, content, rate, writer_email, place_no, img_url, modified_by)
    values ('좋아요', '또 올게요', 5, 'gy011003@gmail.com', 1, '/Users/kwonjiyoung/Documents/multicampus/박보검.jpeg', 'gy011003@gmail.com');

INSERT INTO rev (title, content, rate, writer_email, place_no, img_url, modified_by)
VALUES
    ('맛집 인정', '음식이 정말 맛있었어요! 재방문 의사 100%', 5, 'gy011003@gmail.com', 1, '박보검.jpeg', 'gy011003@gmail.com'),
    ('조용한 분위기', '카페 분위기가 너무 조용해서 공부하기 좋았어요.', 4, 'gy011003@gmail.com', 1, '박보검.jpeg', 'gy011003@gmail.com'),
    ('가격 대비 아쉬움', '맛은 괜찮았지만 가격이 조금 비쌌어요.', 3, 'gy011003@gmail.com', 2, '박보검.jpeg', 'gy011003@gmail.com'),
    ('서비스 굿', '직원분들이 친절해서 기분 좋게 식사했습니다.', 5, 'gy011003@gmail.com', 3, '박보검.jpeg', 'gy011003@gmail.com'),
    ('사진이랑 다름', '사진 보고 기대했는데 실물은 좀 달랐어요.', 2, 'gy011003@gmail.com', 4, '박보검.jpeg', 'gy011003@gmail.com');
