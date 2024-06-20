import torch
import tensorflow as tf

print(torch.cuda.is_available())
print(len(tf.config.list_physical_devices('GPU')) > 0)
print(tf.test.gpu_device_name())
print(tf.__version__)
