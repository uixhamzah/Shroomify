#!/usr/bin/env python
from functions import preprocess
from functions import balance

base_path = "data/edible-and-poisonous-fungi/data"
train_df, test_df, valid_df= preprocess(base_path, .8,.1)

max_samples= 688
min_samples=0
column='labels'
working_dir = r'./data/'
img_size=(300, 500)
ndf=balance(train_df,max_samples, min_samples, column, working_dir, img_size)

label = ndf.groupby('labels')
edible_sporocarp = label.get_group('edible sporocarp')
edible_mushroom_sporocarp = label.get_group('edible mushroom sporocarp')
poisonous_sporocarp = label.get_group('poisonous sporocarp')
poisonous_mushroom_sporocarp =label.get_group('poisonous mushroom sporocarp')

train_edible_sporocarp = list(set(edible_sporocarp['filepaths']))
train_edible_mushroom_sporocarp = list(set(edible_mushroom_sporocarp['filepaths']))
train_poisonous_sporocarp = list(set(poisonous_sporocarp['filepaths']))
train_poisonous_mushroom_sporocarp = list(set(poisonous_mushroom_sporocarp['filepaths']))


train_edible_fungies = list(set(train_edible_sporocarp + train_edible_mushroom_sporocarp))
train_poisonous_fungies = list(set(train_poisonous_sporocarp + train_poisonous_mushroom_sporocarp))

print("\nTrain Edible Fungies\t: ", len(train_edible_fungies))
print("Train Poisonous Fungies\t: ", len(train_poisonous_fungies))

label = valid_df.groupby('labels')
edible_sporocarp = label.get_group('edible sporocarp')
edible_mushroom_sporocarp = label.get_group('edible mushroom sporocarp')
poisonous_sporocarp = label.get_group('poisonous sporocarp')
poisonous_mushroom_sporocarp =label.get_group('poisonous mushroom sporocarp')

valid_edible_sporocarp = list(set(edible_sporocarp['filepaths']))
valid_edible_mushroom_sporocarp = list(set(edible_mushroom_sporocarp['filepaths']))
valid_poisonous_sporocarp = list(set(poisonous_sporocarp['filepaths']))
valid_poisonous_mushroom_sporocarp = list(set(poisonous_mushroom_sporocarp['filepaths']))


valid_edible_fungies = list(set(valid_edible_sporocarp + valid_edible_mushroom_sporocarp))
valid_poisonous_fungies = list(set(valid_poisonous_sporocarp + valid_poisonous_mushroom_sporocarp))

print("Valid Edible Fungies\t: ", len(valid_edible_fungies))
print("Valid Poisonous Fungies\t: ", len(valid_poisonous_fungies))

batch_size = 32
num_batch_per_epoch = min(len(train_edible_fungies), len(train_poisonous_fungies)) // batch_size
print("\nNumber batch per epoch: ",num_batch_per_epoch)