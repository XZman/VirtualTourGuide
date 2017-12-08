
# coding: utf-8

# In[44]:


from camera import VideoCamera
import socket
import numpy as np
from tqdm import tqdm


# In[55]:


vc = VideoCamera(0)
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
dest = ('192.168.43.1',5657)


# In[46]:


def itos(q):
    rs = ''
    for i in range(4):
        b = q & 0xFF
        rs = chr(b) + rs
        q = q >> 8
    return bytes(rs)


# In[56]:


cnt = 0
while True:
    imdata = vc.get_frame()
    imlen = len(imdata)
    imlen_b = itos(imlen)
    s.sendto(imlen_b, dest)
    i = 0
    while i < imlen:
        s.sendto(imdata[i:min(i+5000,imlen)], dest)
        i += 5000

