@echo off
setlocal EnableExtensions
cd /d "%~dp0"

echo Limpiando funcionalidades retiradas de Eruruu Patch...

rem Experimental crafting conflict selector.
if exist "src\main\java\dev\maicra\eruruupatch\client\crafting" rmdir /s /q "src\main\java\dev\maicra\eruruupatch\client\crafting"
if exist "src\main\java\dev\maicra\eruruupatch\crafting" rmdir /s /q "src\main\java\dev\maicra\eruruupatch\crafting"
if exist "src\main\java\dev\maicra\eruruupatch\network" rmdir /s /q "src\main\java\dev\maicra\eruruupatch\network"
if exist "src\main\java\dev\maicra\eruruupatch\mixin\CraftingMenuMixin.java" del /q "src\main\java\dev\maicra\eruruupatch\mixin\CraftingMenuMixin.java"
if exist "src\main\java\dev\maicra\eruruupatch\mixin\ResultSlotMixin.java" del /q "src\main\java\dev\maicra\eruruupatch\mixin\ResultSlotMixin.java"

rem Redundant legacy Argentum seed-acquisition recipes.
if exist "src\main\resources\data\eruruu_patch\recipe\tea_seed_from_short_grass.json" del /q "src\main\resources\data\eruruu_patch\recipe\tea_seed_from_short_grass.json"
if exist "src\main\resources\data\eruruu_patch\recipe\yerba_seed_from_short_grass.json" del /q "src\main\resources\data\eruruu_patch\recipe\yerba_seed_from_short_grass.json"
if exist "src\main\resources\data\eruruu_patch\advancement\recipes\tea_seed_from_short_grass.json" del /q "src\main\resources\data\eruruu_patch\advancement\recipes\tea_seed_from_short_grass.json"
if exist "src\main\resources\data\eruruu_patch\advancement\recipes\yerba_seed_from_short_grass.json" del /q "src\main\resources\data\eruruu_patch\advancement\recipes\yerba_seed_from_short_grass.json"

echo Limpieza completada.
endlocal & exit /b 0
