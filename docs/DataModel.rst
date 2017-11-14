.. MIT License

    Copyright (c) 2017 yasshi2525

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

データモデル
============

データモデルを示します。

設計方針
--------

* 主キーは人工キーではなくナチュラルキーで定義する
* 複合主キーになる場合、サロゲートキーを定義する
* 外部キーはサロゲートキーに対して設定する
* `nullable` なフィールドは定義しない

論理データモデル
----------------

.. er-diagram::

    entities:
        リクエストトークン:
            columns:
                - リクエストトークン : { pk : true }
                - 秘密リクエストトークン
                - 外部サービス名

        アクセストークン:
            columns:
                - リクエストトークン : { pk : true, fk : true }
                - アクセストークン
                - 秘密アクセストークン
                - oAuthVerifier
                - 外部サービスログインID
            
        プレイヤ:
            columns:
                - 外部サービス名 : { pk : true }
                - 外部サービスログインID : { pk : true }
                - プレイヤID
                - プレイヤトークン
                - アクセストークン : { fk : true }

        プレイヤ情報:
            columns:
                - プレイヤID : { pk : true }
                - プレイヤ情報ID

        鉄道経営情報:
            columns:
                - プレイヤID : { pk : true }
                - 鉄道経営情報ID

        線路ノード:
            columns:
                - 所有プレイヤID : { pk : true, fk : true }
                - X座標値 : { pk : true }
                - Y座標値 : { pk : true }
                - 線路ノードID

        線路エッジ:
            columns:
                - 線路ノードFrom : { pk : true, fk : true }
                - 線路ノードTo : { pk : true, fk : true }
                - 線路エッジID
                - 所有プレイヤID : { fk : true }

        駅:
            columns:
                - 所有プレイヤID : { pk : true, fk : true }
                - X座標値 : { pk : true }
                - Y座標値 : { pk : true }
                - 駅ID
                - 駅名

        改札口:
            columns:
                - 駅ID : { pk : true, fk : true }
                - 改札口ID
                - 改札数

        プラットフォーム:
            columns:
                - 駅ID : { pk : true, fk : true }
                - プラットフォームID
                - 線路ノードID : { fk : true }
                - 収容人数

        路線:
            columns:
                - 所有プレイヤID : { pk : true, fk : true }
                - 路線名 : { pk : true }
                - 路線ID
                
        路線ステップ:
            columns:
                - 路線ステップID : { pk : true }
                - 路線ID : { fk : true }
                - 次路線ステップID : { fk : true }

        路線ステップ発車:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 停車中プラットフォームID : { fk : true }

        路線ステップ移動:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 走行線路エッジID : { fk : true }

        路線ステップ停車:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 走行線路エッジID : { fk : true }
                - 到達プラットフォームID : { fk : true }

        電車:
            columns:
                - 所有プレイヤID : { pk : true, fk : true }
                - 電車名 : { pk : true }
                - 電車ID
                - 収容人数
                - 機動力

        電車配置:
            columns:
                - 電車ID : { pk : true, fk : true}
                - 所属路線ID : { fk : true }
                - 現在路線ステップID : { fk : true }

        住宅:
            columns:
                - X座標 : { pk : true }
                - Y座標 : { pk : true }
                - 住宅ID
                - 居住者数

        会社:
            columns:
                - X座標 : { pk : true }
                - Y座標 : { pk : true }
                - 会社ID
                - 従業員数

        人:
            columns:
                - 人ID : { pk : true }
                - 出発住宅ID : { fk : true }
                - 到達会社ID : { fk : true }
                - X座標
                - Y座標
                - 生存カウント
        
        人用移動ステップ直接移動:
            columns:
                - 出発住宅ID : { pk : true }
                - 到達会社ID : { pk : true }
                - 人用移動ステップID : { fk : true }

        人用移動ステップ住宅から駅:
            columns:
                - 出発住宅ID : { pk : true }
                - 到達改札口ID : { pk : true }
                - 人用移動ステップID : { fk : true }
  
        人用移動ステップ駅から会社:
            columns:
                - 出発改札口ID : { pk : true }
                - 到達会社ID : { pk : true }
                - 人用移動ステップID : { fk : true }    

        人用移動ステップ駅入場:
            columns:
                - 出発改札口ID : { pk : true }
                - 到達プラットフォームID : { pk : true }
                - 人用移動ステップID : { fk : true }
      
        人用移動ステップ駅出場:
            columns:
                - 出発プラットフォームID : { pk : true }
                - 到達改札口ID : { pk : true }
                - 人用移動ステップID : { fk : true }

        人用移動ステップ電車移動:
            columns:
                - 出発プラットフォームID : { pk : true }
                - 到達プラットフォームID : { pk : true }
                - 人用移動ステップID : { fk : true }

    relations:
        - リクエストトークン 1--? アクセストークン
        - アクセストークン *--? プレイヤ
        - プレイヤ 1--1 プレイヤ情報
        - プレイヤ 1--* 線路ノード
        - プレイヤ 1--* 線路エッジ
        - プレイヤ 1--* 駅
        - プレイヤ 1--* 路線
        - プレイヤ 1--* 電車
        - 線路ノード 1--* 線路エッジ
        - 線路ノード 1--? プラットフォーム
        - 駅 1--1 改札口
        - 駅 1--1 プラットフォーム
        - 路線 1--* 路線ステップ
        - 路線ステップ 1--1 路線ステップ
        - 路線ステップ 1--? 路線ステップ発車
        - 路線ステップ 1--? 路線ステップ移動
        - 路線ステップ 1--? 路線ステップ停車
        - 路線ステップ発車 *--1 プラットフォーム
        - 路線ステップ移動 *--1 線路エッジ
        - 路線ステップ停車 *--1 線路エッジ
        - 路線ステップ停車 *--1 プラットフォーム
        - 電車 1--? 電車配置
        - 電車配置 *--1 路線
        - 電車配置 *--1 路線ステップ
        - 住宅 1--* 人
        - 会社 1--* 人

        - 住宅 1--* 人用移動ステップ直接移動
        - 会社 1--* 人用移動ステップ直接移動

        - 住宅 1--* 人用移動ステップ住宅から駅
        - 改札口 1--* 人用移動ステップ住宅から駅

        - 会社 1--* 人用移動ステップ駅から会社
        - 改札口 1--* 人用移動ステップ駅から会社

        - プラットフォーム 1--* 人用移動ステップ駅入場
        - 改札口 1--* 人用移動ステップ駅入場

        - プラットフォーム 1--* 人用移動ステップ駅出場
        - 改札口 1--* 人用移動ステップ駅出場

        - プラットフォーム 1--* 人用移動ステップ電車移動
        - 鉄道経営情報 1--1 プレイヤ

.. note::

    Ver 0.0では人の経路情報をデータベースに永続化したが、
    `nullable` なフィールドが増え、条件分岐が複雑になってしまった。
    また、人ごとに経路情報を持つため、パフォーマンスが出なかった。
    そこで Ver 1.0 は性能面の課題を解決するため、経路情報は
    ゲームマスタが管理する仕様とした。

.. note::

    人用移動ステップはデータベースに保存しなくとも、実行時に生成可能。
    しかし、永続化する情報によって決まる値なので、永続化対象にした。

.. todo::
    
    上記ER図と実装に以下の乖離がある。

    * oAuth がサービス名を持たない。
    * oAuth が外部サービスログインIDを持たない。
    * player がサインインタイプを持っている。