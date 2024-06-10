const fs = require("fs");
const path = require("path");
const Response = require("../models/Response");
const unzipper = require("unzipper");
const stream = require("stream");
const archiver = require("archiver");
const HttpStatus = require("../models/HttpStatus");

const addFilesToArchive = async (directoryPath, archivePath, archive) => {
    const files = await fs.promises.readdir(directoryPath);

    for (const file of files) {
        const filePath = path.join(directoryPath, file);
        const archiveFilePath = path.join(archivePath, file);

        const stat = await fs.promises.stat(filePath);

        if (stat.isDirectory()) {
            await addFilesToArchive(filePath, archiveFilePath, archive);
        } else {
            archive.file(filePath, { name: archiveFilePath });
        }
    }
}

const createDirectoriesFromMain = (dirPath) => {
    const directories = dirPath.split(path.sep);
    let currentPath = path.join(__dirname, "..", "uploads", "main");
    directories.forEach((directory) => {
        currentPath = path.join(currentPath, directory);

        if (!fs.existsSync(currentPath)) {
            fs.mkdirSync(currentPath, { recursive: true });
        }
    });
}

const getFile = async (filePath) => {
    try {
        if (!filePath) {
            return new Response(400, true, "Đường dẫn không được để trống", null);
        }

        const mainFilePath = path.join(__dirname, '..', 'uploads', 'main', ...filePath.split('/'));
        if (!fs.existsSync(mainFilePath)) {
            return new Response(404, true, "File không tồn tại", null);
        }

        const fileContent = fs.readFileSync(mainFilePath);
        const byteArray = Uint8Array.from(fileContent);
        return byteArray;
    } catch (error) {
        console.error('Error getting file:', error);
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Lấy file thất bại", null);
    }
}

const storeFile = async (filePath, option, filename, data) => {
    try {
        if (!data) {
            return new Response(400, true, "File không được để trống", null);
        }
        
        const mainDirectoryPath = path.join(__dirname, '..', 'uploads', 'main');

        if (path.extname(filename) === '.zip') {
            if (option === "zip") {
                return new Response(400, true, "Không thể nén file này", null);
            }

            if (option === "default") {
                const mainFilePath = path.join(mainDirectoryPath, ...filePath.split('/'), filename);
                if (fs.existsSync(mainFilePath)) {
                    return new Response(409, true, "File đã tồn tại", null);
                }

                createDirectoriesFromMain(filePath);

                fs.writeFile(filePath, data, 'binary', (err) => {
                    if (err) {
                        return new Response(500, true, "Lưu file thất bại", null);
                    } else {
                        return new Response(201, false, "Lưu file thành công", null);
                    }
                });
                
                return new Response(201, false, "Lưu file thành công", null);
            }

            const mainFilePath = path.join(mainDirectoryPath, ...filePath.split('/'), path.basename(filename, ".zip"));
            if (fs.existsSync(mainFilePath)) {
                return new Response(HttpStatus.CONFLICT, true, "Thư mục đã tồn tại", null);
            }

            createDirectoriesFromMain(filePath);
            const unzipFolderPath = path.join(mainDirectoryPath, ...filePath.split('/'), path.basename(filename, '.zip'));
            const bufferStream = new stream.PassThrough();
            bufferStream.end(data);

            // Pipe the stream to the unzipper
            bufferStream.pipe(unzipper.Extract({ path: unzipFolderPath }))
                .on('error', (err) => {
                    console.error('Error extracting zip:', err);
                })
                .on('finish', () => {
                    console.log('Zip file extracted successfully.');
                });

            const isValidFile = await DiskStorage.checkFileType(unzipFolderPath);
            if (!isValidFile) {
                fs.rmSync(unzipFolderPath, { recursive: true, force: true });
                return new Response(HttpStatus.BAD_REQUEST, true, "File bao gồm những loại tin không hợp lệ!", null);
            }
            
            return new Response(HttpStatus.CREATED, false, "Lưu file thành công", null);
        } else {
            if (option === "unzip") {
                return new Response(HttpStatus.BAD_REQUEST, true, "Không thể giải nén file này", null);
            }
            if (option === "default") {
                const mainFilePath = path.join(mainDirectoryPath, ...filePath.split('/'), filename);
                if (fs.existsSync(mainFilePath)) {
                    return new Response(HttpStatus.CONFLICT, true, "File đã tồn tại", null);
                }

                createDirectoriesFromMain(filePath);

                const fileData = Buffer.from(data, 'base64');
                fs.writeFileSync(mainFilePath, fileData, 'binary');

                return new Response(HttpStatus.CREATED, false, "Lưu file thành công", null);
            }

            const mainFilePath = path.join(mainDirectoryPath, ...filePath.split('/'), filename.replace(/\.[^/.]+$/, '') + ".zip");
            if (fs.existsSync(mainFilePath)) {
                return new Response(HttpStatus.CONFLICT, true, "File đã tồn tại", null);
            }

            const output = fs.createWriteStream(mainFilePath);

            const archive = archiver('zip', {
                zlib: { level: 9 }
            });

            archive.pipe(output);

            archive.append(data, { name: filename });

            archive.finalize();

            output.on('close', () => {
                console.log('Compressed zip file created successfully:', zipFilePath);
            });

            archive.on('end', () => {
                return new Response(HttpStatus.CREATED, false, "Lưu file thành công", null);
            });

            archive.on('error', (err) => {
                console.error('Error occurred while archiving:', err);
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Lưu file thất bại", null);
            });
        }
    } catch (error) {
        console.error('Error storing file:', error);
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Lưu file thất bại", null);
    }
}

const deleteFile = async (filePath) => {
    try {
        const deletedFilePath = path.join(__dirname, "..", "uploads", "main", filePath);

        if (!fs.existsSync(deletedFilePath)) {
            return new Response(HttpStatus.OK, false, "Xoá file thành công", null);
        }

        if (fs.statSync(deletedFilePath).isDirectory()) {
            fs.rmSync(deletedFilePath, { recursive: true, force: true });        
        }
        else {
            fs.unlinkSync(deletedFilePath);
        }

        return new Response(HttpStatus.OK, false, "Xoá file thành công", null);  
    } catch (error) {
        console.log(error);
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, true, "Đã xảy ra lỗi. Xóa file thất bại", null);
    }
};

module.exports = {
    addFilesToArchive,
    createDirectoriesFromMain,
    getFile,
    storeFile,
    deleteFile
}