Add-Type -AssemblyName System.Drawing

$root = 'C:\Users\17046\Downloads\kaleidoscope_sculk-1.2'
$src  = Join-Path $root 'build\cookery150\assets\kaleidoscope_cookery'
$dst  = Join-Path $root 'src\main\resources\assets\kaleidoscope_sculk'

$baseR = 34; $baseG = 78; $baseB = 88
$baseLum = 0.299 * $baseR + 0.587 * $baseG + 0.114 * $baseB

function Convert-TeaPixel([System.Drawing.Color]$c) {
    if ($c.A -lt 8) { return $c }
    $lum = 0.299 * $c.R + 0.587 * $c.G + 0.114 * $c.B
    $k = $lum / $baseLum
    $r = [int][Math]::Min(255, [Math]::Round($baseR * $k))
    $g = [int][Math]::Min(255, [Math]::Round($baseG * $k))
    $b = [int][Math]::Min(255, [Math]::Round($baseB * $k))
    return [System.Drawing.Color]::FromArgb($c.A, $r, $g, $b)
}

function Convert-BagPixel([System.Drawing.Color]$c) {
    if ($c.A -lt 8) { return $c }
    if ($c.G -gt $c.R -and $c.G -gt $c.B) { return (Convert-TeaPixel $c) }
    $r = [int][Math]::Min(255, [Math]::Round($c.R * 0.94))
    $g = [int][Math]::Min(255, [Math]::Round($c.G * 0.97))
    $b = [int][Math]::Min(255, [Math]::Round($c.B * 1.08))
    return [System.Drawing.Color]::FromArgb($c.A, $r, $g, $b)
}

function Write-Recolored([string]$inPath, [string]$outPath, [scriptblock]$converter) {
    $src2 = New-Object System.Drawing.Bitmap($inPath)
    $out = New-Object System.Drawing.Bitmap($src2.Width, $src2.Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y = 0; $y -lt $src2.Height; $y++) {
        for ($x = 0; $x -lt $src2.Width; $x++) {
            $out.SetPixel($x, $y, (& $converter $src2.GetPixel($x, $y)))
        }
    }
    $dir = Split-Path $outPath -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    $out.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $out.Dispose()
    $src2.Dispose()
    "wrote $outPath ($($src2.Width)x$($src2.Height))"
}

$bs = Get-Content (Join-Path $src 'blockstates\barley_tea.json') -Raw
$bs = $bs -replace 'kaleidoscope_cookery:block/teacup/barley_tea/', 'kaleidoscope_sculk:block/teacup/echo_puer/'
[IO.File]::WriteAllText((Join-Path $dst 'blockstates\echo_puer.json'), $bs)
"wrote blockstates\echo_puer.json"

$modelDst = Join-Path $dst 'models\block\teacup\echo_puer'
if (-not (Test-Path $modelDst)) { New-Item -ItemType Directory -Force -Path $modelDst | Out-Null }
Get-ChildItem (Join-Path $src 'models\block\teacup\barley_tea\*.json') | ForEach-Object {
    $c = Get-Content $_.FullName -Raw
    $c = $c -replace 'kaleidoscope_cookery:block/teacup/barley_tea', 'kaleidoscope_sculk:block/teacup/echo_puer'
    [IO.File]::WriteAllText((Join-Path $modelDst $_.Name), $c)
}
"wrote $((Get-ChildItem $modelDst).Count) teacup block models"

Write-Recolored (Join-Path $src 'textures\block\teacup\barley_tea.png') (Join-Path $dst 'textures\block\teacup\echo_puer.png') { param($c) Convert-TeaPixel $c }
Write-Recolored (Join-Path $src 'textures\item\barley_tea.png')        (Join-Path $dst 'textures\item\echo_puer.png')        { param($c) Convert-TeaPixel $c }
Write-Recolored (Join-Path $src 'textures\item\barley_tea_bag.png')    (Join-Path $dst 'textures\item\echo_puer_tea_bag.png') { param($c) Convert-BagPixel $c }

"done"
