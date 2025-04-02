# Database Initialization
# @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

-- Create database
create database if not exists learning_forum;

-- Switch database
use learning_forum;

-- User table
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'account',
    userPassword varchar(512)                           not null comment 'key',
    unionId      varchar(256)                           null comment 'Wechat public platform id',
    mpOpenId     varchar(256)                           null comment 'Wechat public account openId',
    userName     varchar(256)                           null comment 'user name',
    userAvatar   varchar(1024)                          null comment 'user avatar',
    userProfile  varchar(512)                           null comment 'user profile',
    userRole     varchar(256) default 'user'            not null comment 'user role：user/admin/ban',
    editTime     datetime default CURRENT_TIMESTAMP     not null comment 'edit time',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete     tinyint      default 0                 not null comment 'is delete (0: no, 1: yes)',
    vipExpireTime datetime     null comment 'vip expiration time',
    vipCode       varchar(128) null comment 'vip code',
    vipNumber     bigint       null comment 'vip number',

    index idx_unionId (unionId)
) comment 'users' collate = utf8mb4_unicode_ci;

-- Post table
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment 'title',
    content    text                               null comment 'content',
    tags       varchar(1024)                      null comment 'tags(json arrays',
    thumbNum   int      default 0                 not null comment 'thumb count',
    favourNum  int      default 0                 not null comment 'favour count',
    userId     bigint                             not null comment 'create user id',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete   tinyint  default 0                 not null comment 'is delete (0: no, 1: yes)',
    index idx_userId (userId)
) comment 'posts' collate = utf8mb4_unicode_ci;

-- Post thumbs table (hard delete)
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment 'post id',
    userId     bigint                             not null comment 'create user id',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    index idx_postId (postId),
    index idx_userId (userId)
) comment 'post thumbs' collate = utf8mb4_unicode_ci;

-- Post favourites table (hard delete)
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment 'post id',
    userId     bigint                             not null comment 'create user id',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    index idx_postId (postId),
    index idx_userId (userId)
) comment 'post favourites' collate = utf8mb4_unicode_ci;

-- Question bank table
create table if not exists question_bank
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(256)                       null comment 'question title',
    description text                               null comment 'question description',
    picture     varchar(2048)                      null comment 'question picture',
    userId      bigint                             not null comment 'create user id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime  datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete    tinyint  default 0                 not null comment 'is delete (0: no, 1: yes)',
    priority  int  default 0  not null comment 'priority',
    index idx_title (title)
) comment 'questions' collate = utf8mb4_unicode_ci;

-- Question table
create table if not exists question
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(256)                       null comment 'title',
    content    text                               null comment 'content',
    tags       varchar(1024)                      null comment 'tag list (json array)',
    answer     text                               null comment 'recommended answer',
    userId     bigint                             not null comment 'creator user id',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete   tinyint  default 0                 not null comment 'is deleted',
    reviewStatus  int      default 0  not null comment 'status: 0-pending review, 1-approved, 2-rejected',
    reviewMessage varchar(512)        null comment 'review message',
    reviewerId    bigint              null comment 'reviewer id',
    reviewTime    datetime            null comment 'review time',
    viewNum       int      default 0    not null comment 'view count',
    thumbNum      int      default 0    not null comment 'thumb count',
    favourNum     int      default 0    not null comment 'favourite count',
    priority  int  default 0  not null comment 'priority',
    source   varchar(512)  null comment 'question source',
    needVip  tinyint  default 0  not null comment 'vip only (1 means vip only)',
    index idx_title (title),
    index idx_userId (userId)
) comment 'question' collate = utf8mb4_unicode_ci;

-- Question bank question table (hard delete)
create table if not exists question_bank_question
(
    id             bigint auto_increment comment 'id' primary key,
    questionBankId bigint                             not null comment 'question bank id',
    questionId     bigint                             not null comment 'question id',
    userId         bigint                             not null comment 'creator user id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    questionOrder  int  default 0  not null comment 'question order (question number)',
    UNIQUE (questionBankId, questionId)
) comment 'question bank questions' collate = utf8mb4_unicode_ci;