#!/usr/bin/env python
import tensorflow as tf
import tensorflow_hub as hub
import pandas as pd
from functions import get_dataset
from import_balancing_dataset import train_edible_fungies, train_poisonous_fungies, valid_edible_fungies, valid_poisonous_fungies, batch_size


handle_base = "mobilenet_v2"
MODULE_HANDLE ="https://tfhub.dev/google/tf2-preview/{}/feature_vector/4".format(handle_base)
feature_extractor = hub.KerasLayer(MODULE_HANDLE,
                                    input_shape=(224, 224, 3))
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

model.save("shroomify_model.h5")

MUSHROOM_SAVED_MODEL = "mushroom_saved_model"

# Use TensorFlow's SavedModel API to export the SavedModel from the trained Keras model
tf.saved_model.save(model, MUSHROOM_SAVED_MODEL)
loaded = tf.saved_model.load(MUSHROOM_SAVED_MODEL)
print(list(loaded.signatures.keys()))
infer = loaded.signatures["serving_default"]
print(infer.structured_input_signature)
print(infer.structured_outputs)

# Intialize the TFLite converter to load the SavedModel
converter = tf.lite.TFLiteConverter.from_saved_model(MUSHROOM_SAVED_MODEL)

# Set the optimization strategy for 'size' in the converter 
converter.optimizations = [tf.lite.Optimize.OPTIMIZE_FOR_SIZE]

# Use the tool to finally convert the model
tflite_model = converter.convert()