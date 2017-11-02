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

機能
====

RushHourがもつ機能について説明します。
大きく、ユーザが操作する機能と、バックグラウンドで動く機能に大別されます。

ここでは以下のことを記載します。

* できること、できないこと
* 登場人物の説明 [#entity]_

以下のことは記載しません。

* どう実現するか
* 仕様 (○○の場合、できる/できない)

補足事項は本文が長くなるため、脚注に記載します。

.. rubric:: 脚注

.. [#entity] 何に対しての機能か分かるようにするため。

ユーザができること
------------------

* 自分の鉄道を管理する
* 経営による収支を管理する
* マップの様子を眺める
* 他ユーザの経営状況を見る

鉄道の管理
^^^^^^^^^^

鉄道は、線路、駅、電車の3つの物理的要素から構成されます。
電車は線路の上を走ります。乗客は駅で電車に乗り降りします。

論理的な要素として、路線が存在します。
電車は路線に従って運行します。

線路
""""

線路は複線とします [#double_track]_ 。

* 線路を敷設する
* 線路を撤去する

線路は途中で分岐させることができます。
分岐のコントロール方法は仕様の項目で記載します。

.. rubric:: 脚注

.. [#double_track] 運行しづらいため。正面衝突を回避する機能が必要になるため。

駅
""

* 駅を作る
* 駅を撤去する

電車
""""

* 電車を購入する
* 電車を線路に配置する
* 電車を線路から撤去する
* 電車を廃棄する

路線
""""

路線とは、始発駅から終着駅までの経路のことを指します。
途中駅の停車・通過を指定できます。

* 新規路線を作る
* 既存路線の経路を変更する
* 停車駅を変更する

路線機能を使って以下のことができます。

* 各駅停車と急行の2種別で運行する
* 長距離走る電車と、利用の多い区間だけ走る電車を運行する
* 途中駅でY字分岐させ、分岐・合流させる。

例::

    --- -1-> --- -2-> ---
    A駅      B駅      C駅
    --- <-4- --- <-3- ---

路線名「各停」::

    A駅停車 -> 1 -> B駅停車 -> 2 -> C駅停車 -> 3 -> B駅停車 -> 4 -> (最初に戻る)

路線名「急行」::

    A駅停車 -> 1 -> B駅通過 -> 2 -> C駅停車 -> 3 -> B駅通過 -> 4 -> (最初に戻る)
