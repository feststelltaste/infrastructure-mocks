PATTERN=$1
if [ -z $PATTERN ];
then
   $PATTERN=*.sql
fi

SUBDIR=$2

FILES=`find ./sql/import$SUBDIR -type f -name $PATTERN -print | sort | xargs -r0 echo`

echo "set MODE=DB2;" > ./sql/importgenerated.sql
for f in $FILES
do
	echo "added $f"
	cat $f >> ./sql/importgenerated.sql
done

sed -i 's/AS DECIMAL\(.*\)//g' ./sql/importgenerated.sql
sed -i 's/NO ORDER//g' ./sql/importgenerated.sql

sed -i 's/FOR MIXED DATA//g' ./sql/importgenerated.sql
sed -i 's/WITH DEFAULT 0/DEFAULT 0/g' ./sql/importgenerated.sql
sed -i 's/WITH DEFAULT/DEFAULT/g' ./sql/importgenerated.sql

sed -i 's/CALL sysproc\.admin\_cmd\(.*\);//g' ./sql/importgenerated.sql

sed -i 's/999999999999999999999999999/9223372036854775807/g' ./sql/importgenerated.sql

sed -i 's/ALTER TABLE .* DROP RESTRICT ON DROP;//g' ./sql/importgenerated.sql

sed -i 's/START WITH 1000000/START WITH 1100000/g' ./sql/importgenerated.sql

sed -i 's/FREEPAGE 10//g' ./sql/importgenerated.sql

sed -i 's/CLOSE NO//g' ./sql/importgenerated.sql

sed -i 's/NOT NULL DEFAULT/NOT NULL/g' ./sql/importgenerated.sql


java -cp jar/h2*.jar org.h2.tools.RunScript -url jdbc:h2:./db/localDB -script ./sql/importgenerated.sql

