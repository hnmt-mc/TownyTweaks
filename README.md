# TownyTweaks
Minimum Towny Version: `0.100.0.0` \
Minimum SiegeWar Version: `2.9.0` \
Minimum Brewery Version: `3.1`


### Rework by [eartho-project](https://github.com/eartho-project) & [Aomkoyo](https://github.com/aomkoyo)

## Discord通知（日ノ本鯖向けの改造）
- 町・国の作成、町の廃墟化・削除、国の解散、SiegeWar の攻城を、日本語の埋め込みで Discord に通知します（`webhook.yml`）。
- 町の作成には市長の顔、国の作成には王の顔（`mc-heads.net`）を右上に出します。`face-enabled` と `face-url` で変えられます。
- 送信は別スレッドで行い、サーバーを止めません。メンションは一切飛ばしません。
- 動作確認は Towny 0.103 / Paper 1.21.11。ビルドで使う Towny は、手元の jar をローカルの Maven に入れて通しています。

