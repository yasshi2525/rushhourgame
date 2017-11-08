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

アーキテクチャ
==============

ここではRushHourの実装の構成要素を説明します。

以下の3階層から構成されます。

#. Client層
#. Controller層
#. Entity層

3者の関係を以下に示します。

.. blockdiag::
    
    blockdiag {
        "ユーザ,管理者" [shape = "actor"];
        データベース [shape = "flowchart.database"];
        外部サーバ [shape = "cloud"];
        タイマサービス [shape = "roundedbox"];
        ゲームマスタ [shape = "actor"];

        "ユーザ,管理者" <-> Client層 [label = "HTTP"];
        外部サーバ <-> Client層 [label = "HTTP"];
        タイマサービス -> ゲームマスタ -> Controller層;
        Client層 <-> Controller層 <-> Entity層;
        Controller層 <-> "O/Rマッパ" -- データベース;
    }

Client層
--------

Client層は、ソフトウェア内外を結びつけるインタフェースです。

:term:`ユーザ` または :term:`管理者` の
操作を受けつけ、適切なControllerを呼び出し、受け取った結果を出力します。

認証サーバとのHTTP通信などの、外部サービスへの接続もClient層が担います。

Controller層
-------------

.. todo:: Controller層の役割を執筆する

Entity層
-------------

.. todo:: Entity層の役割を執筆する