# Changelog

All notable changes to this project will be documented in this file.

## [1.3.0](https://github.com/nguyentin05/task-management/compare/v1.2.0...v1.3.0) (2026-04-28)

### Features

* **devops:** add performance tests: 1k UV, 500 UV, 120 UV, invalid token, put throught ([#175](https://github.com/nguyentin05/task-management/issues/175)) ([2195b38](https://github.com/nguyentin05/task-management/commit/2195b3830f47315fbb51a9cbc2b64f2ca2d45c10))
* **devops:** update compose and add seed data ([#176](https://github.com/nguyentin05/task-management/issues/176)) ([c308a5d](https://github.com/nguyentin05/task-management/commit/c308a5dce7f0277b80c336355e39be185075fe6f))

### Bug Fixes

* **devops:** add fallback for the first migration to semantic version ([080b32a](https://github.com/nguyentin05/task-management/commit/080b32a2ffb130ff2f1fc02a1edf318bd3e988a0))
* **devops:** fix cd detect changes ([954ec80](https://github.com/nguyentin05/task-management/commit/954ec80bab844af797f8d7ba50437b82ff2d6b67))
* **devops:** fix version for cd workflow ([dd8d821](https://github.com/nguyentin05/task-management/commit/dd8d82193190ec4700cae20d525a0dbefde0b4b7))
* **devops:** read version from artifact for cd ([33f0068](https://github.com/nguyentin05/task-management/commit/33f00682960d9c021f7fb9a6ea008f900b9df22a))
* **fe:** default avatar ([#183](https://github.com/nguyentin05/task-management/issues/183)) ([9a713dd](https://github.com/nguyentin05/task-management/commit/9a713dda262a17aa5f909d66049d67a0f98845b6))

### Dependencies

* **deps:** bump org.projectlombok:lombok from 1.18.44 to 1.18.46 in /backend in the build-tools group ([#181](https://github.com/nguyentin05/task-management/issues/181)) ([af780cb](https://github.com/nguyentin05/task-management/commit/af780cbfc7f690804f910a4c8c897c068637efb0)), closes [#180](https://github.com/nguyentin05/task-management/issues/180) [#179](https://github.com/nguyentin05/task-management/issues/179)

### Documentation

* **report:** update weekly-10 report ([#177](https://github.com/nguyentin05/task-management/issues/177)) ([20a8b27](https://github.com/nguyentin05/task-management/commit/20a8b27bdc28fec50578e4acb6b664d499e56d9b))

## [1.3.0](https://github.com/nguyentin05/task-management/compare/v1.2.0...v1.3.0) (2026-04-28)

### Features

* **devops:** add performance tests: 1k UV, 500 UV, 120 UV, invalid token, put throught ([#175](https://github.com/nguyentin05/task-management/issues/175)) ([2195b38](https://github.com/nguyentin05/task-management/commit/2195b3830f47315fbb51a9cbc2b64f2ca2d45c10))
* **devops:** update compose and add seed data ([#176](https://github.com/nguyentin05/task-management/issues/176)) ([c308a5d](https://github.com/nguyentin05/task-management/commit/c308a5dce7f0277b80c336355e39be185075fe6f))

### Bug Fixes

* **devops:** add fallback for the first migration to semantic version ([080b32a](https://github.com/nguyentin05/task-management/commit/080b32a2ffb130ff2f1fc02a1edf318bd3e988a0))
* **devops:** fix cd detect changes ([954ec80](https://github.com/nguyentin05/task-management/commit/954ec80bab844af797f8d7ba50437b82ff2d6b67))
* **devops:** read version from artifact for cd ([33f0068](https://github.com/nguyentin05/task-management/commit/33f00682960d9c021f7fb9a6ea008f900b9df22a))
* **fe:** default avatar ([#183](https://github.com/nguyentin05/task-management/issues/183)) ([9a713dd](https://github.com/nguyentin05/task-management/commit/9a713dda262a17aa5f909d66049d67a0f98845b6))

### Documentation

* **report:** update weekly-10 report ([#177](https://github.com/nguyentin05/task-management/issues/177)) ([20a8b27](https://github.com/nguyentin05/task-management/commit/20a8b27bdc28fec50578e4acb6b664d499e56d9b))

## [1.2.0](https://github.com/nguyentin05/task-management/compare/v1.1.0...v1.2.0) (2026-04-23)

### Features

* **devops:** auto release by semantic release bot ([#170](https://github.com/nguyentin05/task-management/issues/170)) ([a1a2389](https://github.com/nguyentin05/task-management/commit/a1a238977bdf3cb5d41af115e5b8ce5f5c6b953a))

### Bug Fixes

* **devops:** add permission for cicd job (codeQl bug's) ([#166](https://github.com/nguyentin05/task-management/issues/166)) ([126e5c4](https://github.com/nguyentin05/task-management/commit/126e5c44c0408299b1fe401ab075935ba0337470))
* **devops:** add permission for sonar ([#173](https://github.com/nguyentin05/task-management/issues/173)) ([f7c7af7](https://github.com/nguyentin05/task-management/commit/f7c7af7bc56935a7434ff594c24958862c2beca7))
* **devops:** add token for semantic release bot commit ([a04e041](https://github.com/nguyentin05/task-management/commit/a04e0410e961f80b782a844bc286e16aeac4536a))
* **devops:** fix heal check for comment service ([#165](https://github.com/nguyentin05/task-management/issues/165)) ([50f38fb](https://github.com/nguyentin05/task-management/commit/50f38fb7228fd16170e5cbf2f651b86064c645fd))
* **devops:** setup jdk 21 for semantic release ([88591e0](https://github.com/nguyentin05/task-management/commit/88591e0709dbfd0d256051793187c5f68d4d86f4))

### Dependencies

* **deps:** bump com.tngtech.archunit:archunit-junit5 in /backend ([#169](https://github.com/nguyentin05/task-management/issues/169)) ([8d1760c](https://github.com/nguyentin05/task-management/commit/8d1760c6ded8cdd9f9e732b313d904be619c4e5b))
* **deps:** bump org.springdoc:springdoc-openapi-starter-webmvc-ui from 2.8.5 to 3.0.3 in /backend ([#158](https://github.com/nguyentin05/task-management/issues/158)) ([3729831](https://github.com/nguyentin05/task-management/commit/3729831362984527efc05cae102b93d7c5a77b05))

### Documentation

* **docs:** update adr 034 ([#172](https://github.com/nguyentin05/task-management/issues/172)) ([4dcbaa3](https://github.com/nguyentin05/task-management/commit/4dcbaa3e3d89348cd99d704d7b61108a2d32c09f))
* **docs:** update arc42 documentation ([#171](https://github.com/nguyentin05/task-management/issues/171)) ([5fcc792](https://github.com/nguyentin05/task-management/commit/5fcc792baa905e59c2e218966a1e3e90e2ab0110))
* **report:** update weekly-9 report ([#168](https://github.com/nguyentin05/task-management/issues/168)) ([4e43a6b](https://github.com/nguyentin05/task-management/commit/4e43a6b39acfc7cbeac024a5f84577cc711b52fe))
