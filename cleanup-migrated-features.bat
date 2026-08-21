@echo off
setlocal EnableExtensions
cd /d "%~dp0"
echo ============================================================
echo ERURUU PATCH 1.0.28 - CLEANUP DE FEATURES MIGRADAS
echo ============================================================
echo Eliminando residuos de fuentes anteriores...
echo.
if exist "devlibs\stonecutter_sifting-1.21.1-1.0.0.jar" (
  echo   BORRANDO devlibs\stonecutter_sifting-1.21.1-1.0.0.jar
  del /f /q "devlibs\stonecutter_sifting-1.21.1-1.0.0.jar"
)
if exist "src\main\java\dev\maicra\eruruupatch\block\CutterBlock.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\block\CutterBlock.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\block\CutterBlock.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\blockentity\CutterBlockEntity.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\blockentity\CutterBlockEntity.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\blockentity\CutterBlockEntity.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\client\CutterBlockEntityRenderer.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\client\CutterBlockEntityRenderer.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\client\CutterBlockEntityRenderer.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\client\CutterClientEvents.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\client\CutterClientEvents.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\client\CutterClientEvents.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\client\CutterItemRenderer.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\client\CutterItemRenderer.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\client\CutterItemRenderer.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\client\CutterScreen.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\client\CutterScreen.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\client\CutterScreen.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\AxeActionResolver.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\AxeActionResolver.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\AxeActionResolver.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterLogVariant.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterLogVariant.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterLogVariant.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterVillagerAdapter.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterVillagerAdapter.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CutterVillagerAdapter.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CuttingRecipeResolver.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CuttingRecipeResolver.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\CuttingRecipeResolver.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolInteractionEvents.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolInteractionEvents.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolInteractionEvents.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolSupport.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolSupport.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\FarmerToolSupport.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\OutputSimulator.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\OutputSimulator.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers\OutputSimulator.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\integration\jade\CutterJadeProvider.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\integration\jade\CutterJadeProvider.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\integration\jade\CutterJadeProvider.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\integration\jade\EruruuJadePlugin.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\integration\jade\EruruuJadePlugin.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\integration\jade\EruruuJadePlugin.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\item\CutterItem.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\item\CutterItem.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\item\CutterItem.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\menu\CutterMenu.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\menu\CutterMenu.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\menu\CutterMenu.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\menu\CutterMenus.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\menu\CutterMenus.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\menu\CutterMenus.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\mixin\StonecutterSiftingTablesMixin.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\mixin\StonecutterSiftingTablesMixin.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\mixin\StonecutterSiftingTablesMixin.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\registry\ModBlockEntities.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\registry\ModBlockEntities.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\registry\ModBlockEntities.java"
)
if exist "src\main\java\dev\maicra\eruruupatch\registry\ModBlocks.java" (
  echo   BORRANDO src\main\java\dev\maicra\eruruupatch\registry\ModBlocks.java
  del /f /q "src\main\java\dev\maicra\eruruupatch\registry\ModBlocks.java"
)
if exist "src\main\resources\assets\eruruu_patch\blockstates\cutter.json" (
  echo   BORRANDO src\main\resources\assets\eruruu_patch\blockstates\cutter.json
  del /f /q "src\main\resources\assets\eruruu_patch\blockstates\cutter.json"
)
if exist "src\main\resources\assets\eruruu_patch\models\block\cutter.json" (
  echo   BORRANDO src\main\resources\assets\eruruu_patch\models\block\cutter.json
  del /f /q "src\main\resources\assets\eruruu_patch\models\block\cutter.json"
)
if exist "src\main\resources\assets\eruruu_patch\models\item\cutter.json" (
  echo   BORRANDO src\main\resources\assets\eruruu_patch\models\item\cutter.json
  del /f /q "src\main\resources\assets\eruruu_patch\models\item\cutter.json"
)
if exist "src\main\resources\assets\eruruu_patch\textures\item\empty_knife_slot.png" (
  echo   BORRANDO src\main\resources\assets\eruruu_patch\textures\item\empty_knife_slot.png
  del /f /q "src\main\resources\assets\eruruu_patch\textures\item\empty_knife_slot.png"
)
if exist "src\main\resources\data\eruruu_patch\tags\item\cutter_logs.json" (
  echo   BORRANDO src\main\resources\data\eruruu_patch\tags\item\cutter_logs.json
  del /f /q "src\main\resources\data\eruruu_patch\tags\item\cutter_logs.json"
)
if exist "src\main\resources\data\minecraft\tags\block\mineable\pickaxe.json" (
  echo   BORRANDO src\main\resources\data\minecraft\tags\block\mineable\pickaxe.json
  del /f /q "src\main\resources\data\minecraft\tags\block\mineable\pickaxe.json"
)
if exist "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers" (
  echo   LIMPIANDO DIRECTORIO src\main\java\dev\maicra\eruruupatch\compat\easyfarmers
  rmdir /s /q "src\main\java\dev\maicra\eruruupatch\compat\easyfarmers"
)
if exist "src\main\java\dev\maicra\eruruupatch\integration\jade" (
  echo   LIMPIANDO DIRECTORIO src\main\java\dev\maicra\eruruupatch\integration\jade
  rmdir /s /q "src\main\java\dev\maicra\eruruupatch\integration\jade"
)
echo.
echo Limpieza terminada.
endlocal & exit /b 0
