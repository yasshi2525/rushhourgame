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

.. _gamemaster-spec:

ゲームマスタの仕様
==================

:term:`ゲームマスタ` は定期的に処理を実行して、ゲームの進行を行います。

.. _human-spec:

人の移動仕様
------------

経路探索の仕方について記述します。

.. _train-spec:

電車の走行仕様
--------------

:term:`電車` の走行アルゴリズムを説明します。

電車は :term:`路線` に所属し、路線の経路情報に従って、 :term:`線路` の上を走行します。

電車は以下の2つの状態があります。

#. 停車中
#. 走行中

停車を開始してから一定時間経過したら走り始めます。
走行していて駅についたら停車します。
路線で「通過」に設定されていれば停車せずに通過します。

特定の条件をみたすと、状態が変化します。

状態遷移図::
    
                  ---> 発車する --->
                  |               |
    待機する⇔ 1.停車中         2.走行中 ⇔ 走行する
                  |               |
                  <--- 停車する <---
    

待機する
^^^^^^^^

駅でドアを開いたまま待機します。
発車するまでのカウントを減らします。

電車の状態が停車中で、発車するまでのカウント(発車カウント)が1以上であれば待機します。

発車する
^^^^^^^^

ドアを閉じ、駅から発車します。移動はしません。
電車の状態を停車中から走行中に変更します。

電車の状態が停車中で、発車カウントが0であれば発車します。

走行する
^^^^^^^^

線路の上を移動します。
停車駅に到着する場合、オーバーランしないよう停車駅まで移動します。

電車が走行中で、停車駅に到達しなければ走行し続けます。

停車する
^^^^^^^^

駅に停車し、ドアを開きます。
電車の状態を走行中から停車中に変更します。
発車カウントをセットします。

電車が走行中で、停車駅に到着していれば停車します。

パラメタ
^^^^^^^^

発車カウントは、駅によって変わります。
駅の規模が大きいほど、停車する時間が長くなります。

線路を移動する距離は電車によって変わります。
電車の性能が良いほど、移動距離が長くなります。