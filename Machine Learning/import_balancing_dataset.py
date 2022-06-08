#!/usr/bin/env python
from functions import preprocess
# from functions import balance

base_path = "data/edible-and-poisonous-fungi/data"
train_df, test_df, valid_df= preprocess(base_path, .8,.1)

# max_samples= 688
# min_samples=0
# column='labels'
# working_dir = r'./'
# img_size=(300, 500)
# ndf=balance(train_df,max_samples, min_samples, column, working_dir, img_size)