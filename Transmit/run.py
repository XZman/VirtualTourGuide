
# coding: utf-8

# In[1]:


import subprocess
from time import sleep


# In[ ]:


while True:
    process3 = subprocess.Popen(['./gimbalControl','/dev/cu.usbserial-00000000'])
    process3.communicate()


# In[235]:


get_ipython().magic(u'pinfo sleep')

