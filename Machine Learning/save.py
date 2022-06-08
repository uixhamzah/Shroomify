#!/usr/bin/env python
import tensorflow as tf

MUSHROOM_SAVED_MODEL = "mushroom_saved_model"
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

tflite_model_file = 'mushroom_model.tflite'

with open(tflite_model_file, "wb") as f:
    f.write(tflite_model)