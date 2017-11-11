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

システム構成
============

RushHourはWebアプリケーションです。
外部システムとの関わりを定義します。

RushHour本体である、Webアプリケーション内の構成は :ref:`architecture-spec` で定義します。
図中の機能名の詳細は :ref:`function-spec` で定義します。

.. blockdiag::
    
    blockdiag {
        ユーザ [shape = actor];
        管理者 [shape = actor];
        ユーザ用Webサーバ [label = "Webサーバ"];
        管理者用Webサーバ [label = "Webサーバ"];
        認証サーバ [shape = "cloud"];
        データベース [shape = flowchart.database];
        タイマサービス [shape = roundedbox];

        認証機能 [shape = square];
        鉄道管理機能 [shape = square];
        マップ閲覧機能 [shape = square];
        ゲーム進行機能 [shape = square];
        ゲーム管理機能 [shape = square];


        group {
            label = "RushHour";
            fontsize = 16;
            認証機能, 鉄道管理機能, マップ閲覧機能, ゲーム進行機能, ゲーム管理機能
        }

        ユーザ <-> ユーザ用Webサーバ <-> 認証機能 <-> 認証サーバ;
                  ユーザ用Webサーバ <-> 鉄道管理機能;
                  ユーザ用Webサーバ <-> マップ閲覧機能;
        
        管理者 <-> 管理者用Webサーバ <-> ゲーム管理機能;
        ゲーム進行機能 <- タイマサービス;
        ゲーム管理機能 -> タイマサービス;

        // データベースとの関連矢印を表示すると、
        // 図が煩雑になるので、矢印表示をオフ
        認証機能         <-> データベース [style = none];
        鉄道管理機能     -> データベース [style = none];
        マップ閲覧機能   <- データベース [style = none];
        ゲーム進行機能   -> データベース [style = none];
        ゲーム管理機能   -> データベース [style = none];
    }

.. note ::

    各機能はデータベースとやりとりをしますが、簡略化のため関連線の表記を省略しています。