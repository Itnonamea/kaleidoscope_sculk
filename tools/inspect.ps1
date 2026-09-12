Add-Type -AssemblyName System.Drawing
$files = @('eerie_meat.png','cooked_eerie_meat.png','soul_pancake.png','sculk_dust.png')
$base = 'c:\Users\17046\Downloads\kaleidoscope_sculk-1.2\src\main\resources\assets\kaleidoscope_sculk\textures\item'
foreach($f in $files){
    $p = Join-Path $base $f
    $bmp = New-Object System.Drawing.Bitmap($p)
    $w = $bmp.Width; $h = $bmp.Height
    $c0 = $bmp.GetPixel(0,0)
    $c1 = $bmp.GetPixel($w-1,0)
    $c2 = $bmp.GetPixel(0,$h-1)
    $c3 = $bmp.GetPixel($w-1,$h-1)
    $black = 0; $total = $w*$h
    for($y=0; $y -lt $h; $y++){
        for($x=0; $x -lt $w; $x++){
            $px = $bmp.GetPixel($x,$y)
            if($px.R -eq 0 -and $px.G -eq 0 -and $px.B -eq 0){ $black++ }
        }
    }
    Write-Host ("{0}: {1}x{2} corners=TL:{3} TR:{4} BL:{5} BR:{6} pureBlack={7}/{8}" -f $f,$w,$h,$c0.Name,$c1.Name,$c2.Name,$c3.Name,$black,$total)
    $bmp.Dispose()
}
