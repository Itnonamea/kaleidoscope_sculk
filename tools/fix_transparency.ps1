Add-Type -AssemblyName System.Drawing
$files = @('eerie_meat.png','cooked_eerie_meat.png','soul_pancake.png','sculk_dust.png')
$base = 'c:\Users\17046\Downloads\kaleidoscope_sculk-1.2\src\main\resources\assets\kaleidoscope_sculk\textures\item'
$bakDir = 'c:\Users\17046\Downloads\kaleidoscope_sculk-1.2\texture_backup_v2'
New-Item -ItemType Directory -Force -Path $bakDir | Out-Null
$tol = 48
foreach($f in $files){
    $p = Join-Path $base $f
    $bak = Join-Path $bakDir $f
    if(-not(Test-Path $bak)){ [System.IO.File]::Copy($p,$bak) }
    $bmp = New-Object System.Drawing.Bitmap($p)
    [int]$w = $bmp.Width
    [int]$h = $bmp.Height
    $bg = $bmp.GetPixel(0,0)
    $visited = New-Object 'bool[,]'($w,$h)
    $q = New-Object System.Collections.Generic.Queue[string]
    function IsBg($c,$b){
        return ([math]::Abs([int]$c.R - [int]$b.R) -le $tol) -and ([math]::Abs([int]$c.G - [int]$b.G) -le $tol) -and ([math]::Abs([int]$c.B - [int]$b.B) -le $tol)
    }
    for([int]$x=0; $x -lt $w; $x++){
        if(-not $visited[$x,0] -and (IsBg $bmp.GetPixel($x,0) $bg)){ $visited[$x,0]=$true; $q.Enqueue($x.ToString() + ',0') }
        if(-not $visited[$x,($h-1)] -and (IsBg $bmp.GetPixel($x,($h-1)) $bg)){ $visited[$x,($h-1)]=$true; $q.Enqueue($x.ToString() + ',' + ($h-1)) }
    }
    for([int]$y=0; $y -lt $h; $y++){
        if(-not $visited[0,$y] -and (IsBg $bmp.GetPixel(0,$y) $bg)){ $visited[0,$y]=$true; $q.Enqueue('0,' + $y.ToString()) }
        if(-not $visited[($w-1),$y] -and (IsBg $bmp.GetPixel(($w-1),$y) $bg)){ $visited[($w-1),$y]=$true; $q.Enqueue((($w-1).ToString()) + ',' + $y.ToString()) }
    }
    [int]$cnt=0
    while($q.Count -gt 0){
        $s=$q.Dequeue()
        $parts=$s.Split(',')
        [int]$px=[int]$parts[0]
        [int]$py=[int]$parts[1]
        $c=$bmp.GetPixel($px,$py)
        if(IsBg $c $bg){
            $bmp.SetPixel($px,$py,[System.Drawing.Color]::FromArgb(0,$c.R,$c.G,$c.B))
            $cnt++
            $dirs = @( @(1,0), @(-1,0), @(0,1), @(0,-1) )
            foreach($d in $dirs){
                [int]$nx=$px + $d[0]
                [int]$ny=$py + $d[1]
                if($nx -ge 0 -and $nx -lt $w -and $ny -ge 0 -and $ny -lt $h -and -not $visited[$nx,$ny]){
                    $visited[$nx,$ny]=$true
                    $q.Enqueue($nx.ToString() + ',' + $ny.ToString())
                }
            }
        }
    }
    $tmp = $p + '.tmp'
    $bmp.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    [System.IO.File]::Delete($p)
    [System.IO.File]::Move($tmp, $p)
    Write-Host ("{0}: made {1}/{2} px transparent (bg ref RGB={3},{4},{5})" -f $f,$cnt,($w*$h),$bg.R,$bg.G,$bg.B)
}
Write-Host "DONE"
