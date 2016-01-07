# SoracomSampleApp
SoracomのAPIを使ったサンプルAndroidアプリ。GooglePlayに公開しているアプリのソース。

GooglePlayのURL
<BR>
https://play.google.com/store/apps/details?id=com.lyricaloriginal.soracomsampleapp&hl=ja

SORACOMのホームページ<BR>
https://soracom.jp/

# 現時点で使っているAPI。
・/auth<BR>
認証用API<BR>
・/subscribers<BR>
　登録しているSIM一覧取得<BR>
・/subscribers/{imsi}<BR>
指定したIMSIの情報を取得<BR>
・/subscribers/{imsi}/update_speed_class<BR>
指定したIMSIの速度クラスを変更<BR>
・/subscribers/{imsi}/activate<BR>
指定したIMSIの状態を「使用中」に変更<BR>
・/subscribers/{imsi}/deactivate<BR>
指定したIMSIの状態を「休止中」に変更<BR>
・/stats/air/subscribers/{imsi}<BR>
指定したIMSIの通信量履歴を取得<BR>
<BR>

#LICENSE
Apache License2.0で公開します。

 Copyright 2015 LyricalMaestro(@maestro_L_jp)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
