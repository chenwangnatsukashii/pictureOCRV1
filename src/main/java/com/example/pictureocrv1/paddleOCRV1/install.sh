docker pull registry.baidubce.com/paddlepaddle/paddle:2.5.2
docker run -p 8868:8868 --name paddle_docker -it -v $PWD:/paddle registry.baidubce.com/paddlepaddle/paddle:2.5.2 /bin/bash

apt-get update
apt-get install software-properties-common
add-apt-repository ppa:deadsnakes/ppa

apt install python3.10
ls -l /usr/bin | grep python
rm /usr/bin/python
ln -s /usr/bin/python3.10 /usr/bin/python

pip3 install pillow==10.0.0 -i https://pypi.tuna.tsinghua.edu.cn/simple


#下载python
http://python.p2hp.com/
http://python.p2hp.com/downloads/macos/index.html

#安装paddlepaddle
pip install paddlepaddle==2.5.1 -i https://mirror.baidu.com/pypi/simple

#安装paddleocr
pip install paddleocr -i https://mirror.baidu.com/pypi/simple

#升级pip
python -m pip install --upgrade pip -i https://pypi.douban.com/simple --user

#安装Hub Serving
pip install paddlehub -i https://mirror.baidu.com/pypi/simple

