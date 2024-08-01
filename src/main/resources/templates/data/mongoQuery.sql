-- Open MongoDB Compass  -- query
{ _id: ObjectId('65fc66eea669ce6c93604989') }
{ "metadata.userId": 111101 }
{ userId: 111101 }

{ files_id: ObjectId('647a2fe0709ec81cac8170da') }
{ files_id: ObjectId('647a2da8709ec81cac817074') }
{ files_id: ObjectId('647a2fe0709ec81cac8170dc') }

-- Open Thunder Client or postman

-- GET
http://localhost:8081/api/multimedia/647a2da7709ec81cac816ec2

-- GET
http://localhost:8081/api/allPost?userId=190881&page=0&size=10

-- GET
http://localhost:8081/api/backdrop/190881
