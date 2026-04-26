MERGE (a:Profile {userId: 'alice-123'})
  ON CREATE SET a.firstName = 'Alice',
  a.lastName = 'Nguyen',
  a.dob = date('1999-01-15'),
  a.phone = '0901234567',
  a.avatar = 'https://res.cloudinary.com/dam6k8ezg/image/upload/v1775977979/k1rfh14faekvrhblbqtb.jpg';

MERGE (b:Profile {userId: 'bob-456'})
  ON CREATE SET b.firstName = 'Bob',
  b.lastName = 'Tran',
  b.dob = date('2000-05-20'),
  b.phone = '0907654321',
  b.avatar = 'https://res.cloudinary.com/dam6k8ezg/image/upload/v1775977859/fk8gqfdefxzqcsm6mwdd.jpg';