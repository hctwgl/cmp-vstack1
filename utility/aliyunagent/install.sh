#!/bin/sh
root_dir=`dirname $0`
cd $root_dir
rm -rf build dist aliyunagent.egg-info

python setup.py sdist
pip uninstall -y aliyunagent
pip install  dist/*.tar.gz --force-reinstall
