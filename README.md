RushHour
========

## 概要

RushHourは通勤シミュレーションゲームです。
各プレイヤーは鉄道会社の経営者として路線網を拡大させて行きます。
自分が作った路線に人が群がる様子を見て楽しみます。
マルチプレイなので、ライバルの直ぐ側に路線を敷いて客を奪う妨害工作もできます。

## 前提ソフトウェア

- Linux or Windows
- NodeJS (JavaScriptコードのビルド)
- Java SE Development Kit 8 (Javaコードのビルド)
- Apache Maven (ビルドツール)
- MariaDB (DBサーバ)
- Payara Server (アプリケーションサーバ)

## インストール方法

### DBサーバのセットアップ

MariaDBにユーザを作成する

```CREATE USER 'rushhourgame'@'%' IDENTIFIED BY '<your password>';```
```GRANT alter, create, create temporary tables, create view, delete, drop, grant option, index, insert, select, show view, trigger, update ON rushhourgame.* TO 'rushhourgame'@'%';```

### アプリケーションサーバのセットアップ

Payara ServerにJDBCドライバをコピーする (JDBCドライバは MariaDB Connector/J から入手する)  
コピー先 `<install_dir>/glassfish/lib`

DB情報を登録する  
```
<install_dir>/glassfish/bin/asadmin start-domain  
<install_dir>/glassfish/bin/asadmin create-jdbc-connection-pool --datasourceclassname org.mariadb.jdbc.MariaDbDataSource --restype javax.sql.XADataSource --property user=rushhourgame:password=<your password>:databaseName=rushhourgame:portNumber=3306:serverName=localhost RushHourGamePool  
<install_dir>/glassfish/bin/asadmin create-jdbc-resource --connectionpoolid RushHourGamePool jdbc/RushHourGame  
```

### 設定ファイルを編集する

`src/main/resources/config.properties`に以下の値を設定する  
```
rushhour.twitter.consumerKey=<TwitterのDevelopersサイトで発行した値を記入>  
rushhour.twitter.consumerSecret=<TwitterのDevelopersサイトで発行した値を記入>  
rushhour.twitter.callbackUrl=http://127.0.0.1:<ポート番号>/RushHourGame/faces/callbackTwitter.xhtml  
```

### コンパイル

```
npm install  
gulp  
mvn install  
```

### アプリケーションサーバにデプロイする

```
<install_dir>/glassfish/bin/asadmin deploy target/RushHourGame-1.0-SNAPSHOT.war
```

### ブラウザからアクセスする

```http://localhost:<ポート番号>/RushHourGame```

## テスト

### MariaDBにテスト用のユーザを作成する

```
CREATE USER 'rushhourtest'@'%' IDENTIFIED BY 'rushhourtest';  
GRANT alter, create, create temporary tables, create view, delete, drop, grant option, index, insert, select, show view, trigger, update ON rushhourtest.* TO 'rushhourtest'@'%';
```

### Mavenでテストする

```mvn test```

## Licence

The MIT License

## 作成者

yasshi2525 [Twitter](https://twitter.com/yasshi2525)