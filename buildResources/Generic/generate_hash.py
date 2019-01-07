import io, os, sys, hashlib, glob
from os.path import dirname, join, basename, normpath

PATH_TO_ARTEFACTS = "../../build/artefacts/"


def give_hash(file_in, with_this):
    with io.open(file_in, 'rb') as f:
        return with_this(f.read()).hexdigest()  

path_to_artefact = normpath(join(dirname(__file__), PATH_TO_ARTEFACTS))

files = [fn for fn in glob.glob(normpath(join(dirname(__file__), PATH_TO_ARTEFACTS)+'/*'))]

txt256, files_sha256 = "", list(map(lambda fn : give_hash(fn, hashlib.sha256), files))
for i, fn in enumerate(files):
        txt256 += f"{files_sha256[i]} {basename(fn)}\n"

with open(normpath(path_to_artefact + "/sums.sha256"), "w") as text_file:
    print(txt256, file=text_file)

txt512, files_sha512 = "", list(map(lambda fn : give_hash(fn, hashlib.sha256), files))
for i, fn in enumerate(files):
        txt512 += f"{files_sha256[i]} {basename(fn)}\n"

with open(normpath(path_to_artefact + "/sums.sha512"), "w") as text_file:
    print(txt512, file=text_file)


print("SHA 256 ==========================================================================")
print(txt256)
print("SHA 512 ==========================================================================")
print(txt512)

print("complete")

