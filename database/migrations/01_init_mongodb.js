db.getSiblingDB('comment_db').createUser({
    user: 'root',
    pwd: 'root',
    roles: [{ role: 'readWrite', db: 'comment_db' }]
})