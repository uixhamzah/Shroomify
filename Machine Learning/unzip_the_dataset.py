#!/usr/bin/env python3
import zipfile
#local_zip = '/content/mushrooms-classification-common-genuss-images.zip'
#local_zip = '/content/edible-and-poisonous-fungi.zip'
local_zip = 'edible-and-poisonous-fungi.zip'
zip_ref   = zipfile.ZipFile(local_zip, 'r')
zip_ref.extractall('datasets/edible-and-poisonous-fungi/data')
zip_ref.close()
