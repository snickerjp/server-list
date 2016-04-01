# ServerList
* 物理サーバと仮想サーバをまとめて管理するためのツールです。
* ローカルネットワーク内での利用を想定しているため、セキュリティはあまり考慮されていません。

## 使い方
### すぐ動かしたい方
* [リリースページ](https://github.com/shitai246/server-list/releases) から
war ファイルをダウンロードして Tomcat の webapps 配下に置き、Tomcat を起動。
* http://localhost:8080/serverlist/ にアクセス。


### DB 接続先の変更方法
* デフォルトでは H2Database を利用します。他の DB を使いたい場合は /src/main/resources/props/default.props を修正し、リビルドしてください。

## ビルド手順
```
git clone https://github.com/shitai246/server-list.git
cd server-list
./sbt package
```
* 上記を実行すると `target/scala-2.10/` ディレクトリ配下に war ファイルが生成される。
* war ファイルの作成後は *使い方* 参照。
ただし、URL のパスは war のファイル名と同じになるため、適宜変更してください。

## API
* データの取得・更新用の API があります。
* 例によってセキュリティはあまり考慮されていません。

### 共通仕様
* 認証等はありません。
* GET/POST どちらでも可。
* リクエストは HTTP でパラメータをつけてください。
* レスポンスは json で返却されます。

### /v1/GetServerData/api.json
* サーバ一覧を取得するAPIです。
#### Request
|パラメータ名|必須|説明|
|---|---|---|
|keyword|任意|キーワードで全文検索します。|
|runningOnly|任意| 1 を設定すると稼働状況が Running のもののみを検索します。|
|service|任意|サービスID が一致するもののみを検索します。サービスID は Web 画面右上の歯車から確認できます。|

#### Response
|フィールド名|説明|
|---|---|
|id|ユニークキー|
|service|サービスID|
|dataCenter|データセンター|
|rackNumber|ラックナンバー|
|assetNumber|資産管理番号|
|brandName|製品名/親ホスト名|
|hostName|ホスト名|
|localIpAddress|ローカルIP|
|runningFlg|稼働状況。1:Running, 2: Stop, 3:HotStandby, 4:ColdStandby, 5:Buildiing, 6:ServiceOut|
|warrantyPeriod|保証期間|
|lastBackupDate|バックアップ日|
|description|備考|
|tags|タグ|

### /v1/UpdateServerData/update.json
* サーバ情報を更新するAPIです。
* ホスト名とIPアドレスをキーに、渡されたパラメータのみ更新します。（ホスト名とIPアドレスは更新できません）
* 該当するサーバが複数ある場合にはエラーとなります。

#### Request
|パラメータ名|必須|説明|
|---|---|---|
|hostName|必須|ホスト名|
|localIpAddress|必須|ローカルIP|
|service|任意|サービスID|
|dataCenter|任意|データセンター|
|rackNumber|任意|ラックナンバー|
|assetNumber|任意|資産管理番号|
|brandName|任意|製品名/親ホスト名|
|runningFlg|任意|稼働状況。1:Running, 2: Stop, 3:HotStandby, 4:ColdStandby, 5:Buildiing, 6:ServiceOut|
|warrantyPeriod|任意|保証期間。yyyyMMdd で指定|
|lastBackupDate|任意|バックアップ日。yyyyMMdd で指定|
|description|任意|備考|
|tags|任意|タグ|

