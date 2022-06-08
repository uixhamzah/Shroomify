#!/usr/bin/env python
import tensorflow as tf
import tensorflow_hub as hub
import pandas as pd
from functions import get_dataset
from import_balancing_dataset import train_edible_fungies, train_poisonous_fungies, valid_edible_fungies, valid_poisonous_fungies, batch_size


IMAGE_SIZE = 224
handle_base = "mobilenet_v2"
MODULE_HANDLE ="https://tfhub.dev/google/tf2-preview/{}/feature_vector/4".format(handle_base)
feature_extractor = hub.KerasLayer(MODULE_HANDLE,
                                    input_shape=(IMAGE_SIZE, IMAGE_SIZE, 3))
feature_extractor.trainable = False

tf.keras.backend.clear_session()
model = tf.keras.Sequential([
    feature_extractor,
    tf.keras.layers.Dropout(0.3),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dropout(0.3),
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dropout(0.3),
    tf.keras.layers.Dense(2, activation='softmax')
])
model.summary()

model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

EPOCHS = 50
train_dataset = get_dataset(train_edible_fungies, train_poisonous_fungies, "train", batch_size)
valid_dataset = get_dataset(valid_edible_fungies, valid_poisonous_fungies, "valid", batch_size)
history = model.fit(train_dataset,
                    epochs=EPOCHS,
                    validation_data=valid_dataset)

#Accuracy and Loss during training:
history_frame = pd.DataFrame(history.history)
history_frame.loc[:, ['loss', 'val_loss','accuracy', 'val_accuracy']].plot()

