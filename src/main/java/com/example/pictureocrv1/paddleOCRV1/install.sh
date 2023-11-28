docker pull registry.baidubce.com/paddlepaddle/paddle:2.5.2
docker run -p 8868:8868 --name paddle_docker -it -v $PWD:/paddle registry.baidubce.com/paddlepaddle/paddle:2.5.2 /bin/bash

apt-get update
apt-get install software-properties-common
add-apt-repository ppa:deadsnakes/ppa

apt install python3.10
ls -l /usr/bin | grep python
rm /usr/bin/python
ln -s /usr/bin/python3.10 /usr/bin/python

pip install pillow==10.0.0 -i https://pypi.tuna.tsinghua.edu.cn/simple
