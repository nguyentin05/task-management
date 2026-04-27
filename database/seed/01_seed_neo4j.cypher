MERGE (a:Profile {userId: '89a54de8-6090-4248-ba8b-7a8399b918c8'})
ON CREATE SET
  a.id = 'a1988cbd-5507-4b55-8fd5-659e627729d0',
  a.firstName = 'Alice',
  a.lastName = 'Nguyen',
  a.dob = date('1999-01-15'),
  a.phone = '0901234567',
  a.avatar = 'https://res.cloudinary.com/dam6k8ezg/image/upload/v1775977979/k1rfh14faekvrhblbqtb.jpg';

MERGE (b:Profile {userId: 'aaf5d938-8871-41fd-ba32-8c87218d50e0'})
ON CREATE SET
  b.id = 'afd7f10a-086e-4ca6-9686-a02bae33aa3f',
  b.firstName = 'Bob',
  b.lastName = 'Tran',
  b.dob = date('2000-05-20'),
  b.phone = '0907654321',
  b.avatar = 'https://res.cloudinary.com/dam6k8ezg/image/upload/v1775977859/fk8gqfdefxzqcsm6mwdd.jpg';
