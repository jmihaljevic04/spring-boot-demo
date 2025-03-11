db = db.getSiblingDB("petHub");

db.createUser({
    user: "pet-admin",
    pwd: "pet-password",
    roles: [{role: "dbOwner", db: "petHub"}]
});

// initialize DB
db.dummyCollection.insertOne({initialized: true});
