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

.. _Glossary:

用語集
======

.. glossary::

    ユーザ
        ゲームのプレイヤー。アカウントに紐づく。
        :term:`鉄道` を運営・管理できる。

    管理者
        サーバが稼働し続けるように管理する人。
        ゲームが進行するために、特権操作を行うことができる。

    ゲームマスタ
        ゲームの進行を担う主体。人ではなく、ゲームスレッドのことを指す。
        タイマサービスによって、定期的に処理を実行する。

    鉄道
        :term:`線路` 、:term:`駅` 、:term:`電車` 、:term:`路線` をひとまとめにしたもの。
        1 :term:`ユーザ` 1鉄道所有することができる。

    鉄道資産
        :term:`ユーザ` が所有する :term:`線路` 、:term:`駅` 、:term:`電車` 、:term:`路線` のこと。

    線路
        :term:`電車` が走るもの。上り線、下り線の2本の線から構成される。左側通行。

    駅
        :term:`電車` が停車するもの。
        :term:`人` が電車に乗り降りできる場所。
        
    プラットホーム
        駅の中にあり、:term:`人` が電車に乗り降りする場所。

    路線
        :term:`線路` と :term:`駅` から構成される経路情報。

    電車
        :term:`線路` の上を走るもの。
        1つの :term:`路線` に所属しており、路線の経路に従って線路の上を走る。

    人
        RushHourの住民。
        :term:`住宅` から :term:`会社` へ移動する。
        目的地までの移動経路は自分で決める。
        電車に乗ると :term:`乗客` になる。

    乗客
        電車に乗っている :term:`人` 。:term:`駅` で乗り降りする。

    住宅
        :term:`人` が住んでいる場所。
        ここから無限に人が生成され続ける。

    会社
        :term:`人` が勤めている場所。
        ここに到着すると人は消滅する。