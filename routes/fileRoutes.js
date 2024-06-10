const express = require('express');
const DiskStorage = require("../models/DiskStorage");
const fileController = require("../controllers/fileController");

const router = express.Router();

const diskStorage = new DiskStorage("file", 100 * 1024 * 1024, 256, "../uploads/temp", "../uploads/main");

router.get("/", fileController.get);
router.get("/check", fileController.checkExistPath);
router.post("/upload", diskStorage.upload.single(diskStorage.fieldName), fileController.upload);
router.delete("/delete", fileController.remove);
router.get("/create_directory", fileController.createDirectory);

module.exports = router;