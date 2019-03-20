from shutil import copytree, rmtree
import os

if os.path.exists(r"D:\TEMP\db"):
    rmtree(r"D:\TEMP\db")
copytree(r"C:\Users\Sabry CHEIKHROUHOU\Dropbox\Perso\HattrickOrganizer\db", r"D:\TEMP\db")
if os.path.exists(r"D:\Perso\Code\HO\db"):
    rmtree(r"D:\Perso\Code\HO\db")
copytree(r"C:\Users\Sabry CHEIKHROUHOU\Dropbox\Perso\HattrickOrganizer\db", r"D:\Perso\Code\HO\db")