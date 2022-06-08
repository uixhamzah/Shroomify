#!/usr/bin/env python
import os
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
from numpy import block
# Displaying examples from each class
nrows = 2
ncols = 2

pos = 0
base_path = "data/edible-and-poisonous-fungi/data"
for subfolder in os.listdir(base_path):
    
    image_file = os.listdir(os.path.join(base_path, subfolder))[0]
    
    fig = plt.gcf()
    fig.set_size_inches(ncols * 4, nrows * 4)

    pos += 1
    sp = plt.subplot(nrows, ncols, pos)

    cur_image = mpimg.imread(os.path.join(base_path, subfolder, image_file))
    plt.imshow(cur_image)
    plt.title(subfolder)
    plt.axis('Off')
    
plt.show()