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
                - プレイヤトークン
                - アクセストークン : { fk : true }

        プレイヤ情報:
            columns:
                - プレイヤID : { pk : true }

        線路ノード:
            columns:
                - 所有者 : { pk : true, fk : true }
                - X座標値 : { pk : true }
                - Y座標値 : { pk : true }

        線路エッジ:
            columns:
                - 線路ノードFrom : { pk : true, fk : true }
                - 線路ノードTo : { pk : true, fk : true }
                - 所有者 : { fk : true }

        駅:
            columns:
                - 所有者 : { pk : true, fk : true }
                - X座標値 : { pk : true }
                - Y座標値 : { pk : true }
                - 駅名

        駅改札口:
            columns:
                - 駅ID : { pk : true, fk : true }

        駅プラットフォーム:
            columns:
                - 駅ID : { pk : true, fk : true }
                - 線路ポイントID : { fk : true }
                - 収容人数

        路線:
            columns:
                - 所有者 : { pk : true, fk : true }
                - 路線名 : { pk : true }
                
        路線ステップ:
            columns:
                - 路線ID : { fk : true }
                - 次ステップ : { fk : true }

        路線ステップ発車:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 停車中プラットフォームID : { fk : true }

        路線ステップ移動:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 走行線路エッジ : { fk : true }

        路線ステップ停車:
            columns:
                - 路線ステップID : { pk : true, fk : true }
                - 走行線路エッジ : { fk : true }
                - 到達プラットフォームID : { fk : true }

        電車:
            columns:
                - 所有者 : { pk : true, fk : true }
                - 電車名 : { pk : true }
                - 収容人数
                - 機動力

        電車配置:
            columns:
                - 電車ID : {pk : true, fk : true}
                - 所属路線 : { fk : true }
                - 現在路線ステップ : { fk : true }

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
        - 線路ノード 1--? 駅プラットフォーム
        - 駅 1--1 駅改札口
        - 駅 1--1 駅プラットフォーム
        - 路線 1--* 路線ステップ
        - 路線ステップ 1--? 路線ステップ発車
        - 路線ステップ 1--? 路線ステップ移動
        - 路線ステップ 1--? 路線ステップ停車
        - 路線ステップ発車 *--1 駅プラットフォーム
        - 路線ステップ移動 *--1 線路エッジ
        - 路線ステップ停車 *--1 線路エッジ
        - 路線ステップ停車 *--1 駅プラットフォーム
        - 電車 1--? 電車配置
        - 電車配置 *--1 路線
        - 電車配置 *--1 路線ステップ

.. todo::
    
    上記ER図と実装に以下の乖離がある。

    * oAuth がサービス名を持たない。
    * oAuth が外部サービスログインIDを持たない。
    * player がサインインタイプを持っている。
    * プレイヤ情報が OwnerInfo になっている。
    * absorber, distributer が OwnableEntity
    * 線路ノードが RailPoint
    * 線路エッジが Rail
    * 駅舎とプラットフォームが同じStation