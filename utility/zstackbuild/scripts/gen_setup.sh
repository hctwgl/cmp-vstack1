#!/bin/bash
cat >$3 <<EOF
#!/bin/bash
line=\`wc -l \$0|awk '{print \$1}'\`
line=\`expr \$line - 11\`
tmpdir=\`mktemp\`
/bin/rm -f \$tmpdir
mkdir -p \$tmpdir
tail -n \$line \$0 |tar x -C \$tmpdir
cd \$tmpdir
PRODUCT_NAME=$1 PRODUCT_VERSION=$2 bash ./install.sh -a -f zstack*.tgz \$*
ret=\$?
rm -rf \$tmpdir
exit \$ret
EOF
