# Remove lines 852-2410 (1-based) from MOThreadPool.kt (private methods moved to services)
path = "src/main/java/com/infra/mo/skt_giphttp_mo/config/threadPool/MOThreadPool.kt"
with open(path, "r", encoding="utf-8") as f:
    lines = f.readlines()
# Keep lines 1-851 and 2411-end (0-based: 0-850 and 2410-end)
before = lines[:851]
after = lines[2410:]
with open(path, "w", encoding="utf-8", newline="") as f:
    f.writelines(before)
    f.writelines(after)
