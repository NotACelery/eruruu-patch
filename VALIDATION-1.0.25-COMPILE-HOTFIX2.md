# Eruruu Patch 1.0.25 — compile hotfix validation

This hotfix only removes the stale `ModCreativeTabs::onBuildCreativeTab` registration that caused `compileJava` to fail.

## Validate
- Run `build-dev.bat`.
- `:compileJava` must pass the previous `EruruuPatch.java:25` error.
- The remaining deprecation warnings are non-fatal.
- After build, smoke-test Cutter visibility in JEI and Cutter outputs in Jade; no related implementation files were modified by this hotfix.
