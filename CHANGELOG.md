# Changelog

## [6.1.1+1.19.4](https://github.com/axieum/authme/compare/v6.1.0...v6.1.1+1.19.4) (2023-07-07)


### Bug Fixes

* **lang:** update Traditional Chinese ([#92](https://github.com/axieum/authme/issues/92)) ([5323b41](https://github.com/axieum/authme/commit/5323b41513b6506156087facbf69f4e322687588))
* **microsoft:** 'Chat disabled due to missing profile public key' error ([#91](https://github.com/axieum/authme/issues/91)) ([e4574bf](https://github.com/axieum/authme/commit/e4574bf0e6e31aeffa4f4b5edaa63eaa51941777))

## [6.1.0](https://github.com/axieum/authme/compare/v6.0.0...v6.1.0) (2023-06-07)


### Features

* **microsoft:** `ctrl + click` the Microsoft button to choose account (closes [#46](https://github.com/axieum/authme/issues/46)) ([#84](https://github.com/axieum/authme/issues/84)) ([d63c490](https://github.com/axieum/authme/commit/d63c490cdde4151f2508036c5cff2599dba199ae))


### Bug Fixes

* **microsoft:** update Microsoft OAuth2 token endpoints (fixes [#57](https://github.com/axieum/authme/issues/57)) ([#83](https://github.com/axieum/authme/issues/83)) ([e606c2a](https://github.com/axieum/authme/commit/e606c2ae7cbab004d90d1b06a2b6f81675454f27))

## [6.0.0](https://github.com/axieum/authme/compare/v5.0.0...v6.0.0) (2023-03-28)


### ⚠ BREAKING CHANGES

* upgrade to Minecraft 1.19.4 ([#78](https://github.com/axieum/authme/issues/78))

### Miscellaneous Chores

* upgrade to Minecraft 1.19.4 ([#78](https://github.com/axieum/authme/issues/78)) ([97c0a1a](https://github.com/axieum/authme/commit/97c0a1ad6a547a0718575bb7ba2f21956524e99c))

## [5.0.0](https://github.com/axieum/authme/compare/v4.2.0...v5.0.0) (2022-12-14)


### ⚠ BREAKING CHANGES

* Drops support for Minecraft <= 1.19.2

### Features

* upgrade to Minecraft 1.19.3 (fixes [#70](https://github.com/axieum/authme/issues/70)) ([#71](https://github.com/axieum/authme/issues/71)) ([9753f1a](https://github.com/axieum/authme/commit/9753f1ac8b5de8a0adec7b369e69845a699e56a6))

## [4.2.0](https://github.com/axieum/authme/compare/v4.1.0...v4.2.0) (2022-10-08)


### Features

* upgrade `cloth-config-fabric` (7 -> 8) ([2d9e00a](https://github.com/axieum/authme/commit/2d9e00a3bfaf251a6cce3b432580b805ea0f0d6d))


### Bug Fixes

* 'IS YOU' splash text should reflect the new username (fixes [#65](https://github.com/axieum/authme/issues/65)) ([#67](https://github.com/axieum/authme/issues/67)) ([2fc0ab2](https://github.com/axieum/authme/commit/2fc0ab2c060dc91814814d0dcef1839e4dcd2531))
* re-login button should appear on the disconnected screen (fixes [#63](https://github.com/axieum/authme/issues/63)) ([#64](https://github.com/axieum/authme/issues/64)) ([f0bf6e2](https://github.com/axieum/authme/commit/f0bf6e28564cbdf834123be502f05972de9059c3))
* unable to join servers with chat verification turned on (fixes [#60](https://github.com/axieum/authme/issues/60)) ([#66](https://github.com/axieum/authme/issues/66)) ([d8ee98d](https://github.com/axieum/authme/commit/d8ee98d081754f864735544390b7c773c31723df))

## [4.1.0](https://github.com/axieum/authme/compare/v4.0.0...v4.1.0) (2022-07-26)


### Features

* add a warning screen when custom Microsoft auth URLs are in use ([#55](https://github.com/axieum/authme/issues/55)) ([563b8e7](https://github.com/axieum/authme/commit/563b8e721923727e1621ae191d90430fcced4013))
* add traditional Chinese translation ([#52](https://github.com/axieum/authme/issues/52)) ([423c495](https://github.com/axieum/authme/commit/423c4955c4394fba4f0840cc93bff6103c348b5d))

## [4.0.0](https://github.com/axieum/authme/compare/v3.1.0...v4.0.0) (2022-06-08)


### ⚠ BREAKING CHANGES

* upgrade Minecraft (1.18.2 -> 1.19) (#48)

### Features

* upgrade Minecraft (1.18.2 -> 1.19) ([#48](https://github.com/axieum/authme/issues/48)) ([3f6541a](https://github.com/axieum/authme/commit/3f6541ae38eb44f07d9193d2cc1ee24da8701216))

## [3.1.0](https://github.com/axieum/authme/compare/v3.0.0...v3.1.0) (2022-04-17)


### Features

* add Finnish translation ([#45](https://github.com/axieum/authme/issues/45)) ([0924997](https://github.com/axieum/authme/commit/092499713c03526f50f5c20c360443c0fa679acb))

## [3.0.0](https://github.com/axieum/authme/compare/v2.2.0...v3.0.0) (2022-03-31)


### ⚠ BREAKING CHANGES

* use `googleapis/release-please` for releases
* no longer works on Minecraft 1.18.1

### Features

* add French translation ([#37](https://github.com/axieum/authme/issues/37)) ([d64353b](https://github.com/axieum/authme/commit/d64353bd757b550b865365a6486c033bcb4ca536))
* add Polish translation ([#36](https://github.com/axieum/authme/issues/36)) ([00fa1bc](https://github.com/axieum/authme/commit/00fa1bc7be2b3d195e912d6569acf1ce24d8f10e))
* upgrade Minecraft (1.18.1 -> 1.18.2) ([#41](https://github.com/axieum/authme/issues/41)) ([717c24f](https://github.com/axieum/authme/commit/717c24ff2d47362b32bead6f38bf003820149b8a))


### Bug Fixes

* do not rerun the Microsoft login task when resizing the window ([#38](https://github.com/axieum/authme/issues/38)) ([0fb8842](https://github.com/axieum/authme/commit/0fb8842e59312864994d50e9007e5d9adc5a6a7e))


### Continuous Integration

* use `googleapis/release-please` for releases ([717c24f](https://github.com/axieum/authme/commit/717c24ff2d47362b32bead6f38bf003820149b8a))
