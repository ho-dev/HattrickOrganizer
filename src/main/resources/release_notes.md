---
title: HO release notes
layout: page
---

Changelist HO! 1.436
====================
[Added]
- possibility for user to select and save release channel preference (DEV/BETA/STABLE)
- display release notes before HO update
- translation in csv player export
- support for new HT layout by HT Copy Button in Transfer Scout (deadline and price data are not available)
- team transfer update now includes individual histories
- information that player has been fired in transfer history panel
- remove transfer button in transfer history panel
- include direct link to download now version for mac user

[Fixed]
- optimized build is saving approximately 35% disk space on produced binaries (bug #71)
- ability to start HO on Linux from script after fresh install (bug #34)
- version in splash screen and main GUI header (bug #36)
- missing icon (bug #37)
- missing columns in certain languages because of duplicate names (Player Analysis Tab) (bug #41)
- poor quality shirt numbers on MacOS
- wrong data in csv player export tool  (bug #42)
- layout changes in transfer history tab
- fired player transfer history in no longer removed on transfer history update
- player data is no longer missing from transfer history top panel  (bug #45)
- wrong link in menu bout HO!  (bug #43)
- small bugs related to update process on all platforms (bug #57)

[Changed]
- moved 'save HRF dialog' option to download tab (#80)
- moved (and disabled) legacy 'update HO on download' option to release channel tab (#80)
- appearence of release channel tab

[Removed]
- legacy options from preferences check tab (including the tab) (#80)
