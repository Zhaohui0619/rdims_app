package com.hotdog.hdlibrary.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.hotdog.hdlibrary.HDRuntimeContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

public class FileUtils {

	/**
	 * Instances should NOT be constructed in standard programming.
	 */
	public FileUtils() {
		super();
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

	/**
	 * An empty array of type <code>File</code>.
	 */
	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	//-----------------------------------------------------------------------
	/**
	 * Opens a {@link FileInputStream} for the specified file, providing better
	 * error messages than simply calling <code>new FileInputStream(file)</code>.
	 * <p>
	 * At the end of the method either the stream will be successfully opened,
	 * or an exception will have been thrown.
	 * <p>
	 * An exception is thrown if the file does not exist.
	 * An exception is thrown if the file object exists but is a directory.
	 * An exception is thrown if the file exists but cannot be read.
	 *
	 * @param file  the file to open for input, must not be <code>null</code>
	 * @return a new {@link FileInputStream} for the specified file
	 * @throws FileNotFoundException if the file does not exist
	 * @throws IOException if the file object is a directory
	 * @throws IOException if the file cannot be read
	 * @since Commons IO 1.3
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	//-----------------------------------------------------------------------
	/**
	 * Opens a {@link FileOutputStream} for the specified file, checking and
	 * creating the parent directory if it does not exist.
	 * <p>
	 * At the end of the method either the stream will be successfully opened,
	 * or an exception will have been thrown.
	 * <p>
	 * The parent directory will be created if it does not exist.
	 * The file will be created if it does not exist.
	 * An exception is thrown if the file object exists but is a directory.
	 * An exception is thrown if the file exists but cannot be written to.
	 * An exception is thrown if the parent directory cannot be created.
	 *
	 * @param file  the file to open for output, must not be <code>null</code>
	 * @return a new {@link FileOutputStream} for the specified file
	 * @throws IOException if the file object is a directory
	 * @throws IOException if the file cannot be written to
	 * @throws IOException if a parent directory needs creating but that fails
	 * @since Commons IO 1.3
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file + "' could not be created");
				}
			}
		}
		return new FileOutputStream(file);
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a human-readable version of the file size, where the input
	 * represents a specific number of bytes.
	 *
	 * @param size  the number of bytes
	 * @return a human-readable display value (includes units)
	 */
	public static String byteCountToDisplaySize(long size) {
		String displaySize;

		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	//-----------------------------------------------------------------------
	/**
	 * Implements the same behaviour as the "touch" utility on Unix. It creates
	 * a new file with size 0 or, if the file exists already, it is opened and
	 * closed without modifying it, but updating the file date and time.
	 * <p>
	 * NOTE: As from v1.3, this method throws an IOException if the last
	 * modified date of the file cannot be set. Also, as from v1.3 this method
	 * creates parent directories if they do not exist.
	 *
	 * @param file  the File to touch
	 * @throws IOException If an I/O problem occurs
	 */
	public static void touch(File file) throws IOException {
		if (!file.exists()) {
			OutputStream out = openOutputStream(file);
			IOUtils.closeSilently(out);
		}
		boolean success = file.setLastModified(System.currentTimeMillis());
		if (!success) {
			throw new IOException("Unable to set the last modification time for " + file);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Converts a Collection containing java.io.File instanced into array
	 * representation. This is to account for the difference between
	 * File.listFiles() and FileUtils.listFiles().
	 *
	 * @param files  a Collection containing java.io.File instances
	 * @return an array of java.io.File
	 */
	public static File[] convertFileCollectionToFileArray(Collection files) {
		return (File[]) files.toArray(new File[files.size()]);
	}

	//-----------------------------------------------------------------------

	//-----------------------------------------------------------------------
	/**
	 * Converts an array of file extensions to suffixes for use
	 * with IOFileFilters.
	 *
	 * @param extensions  an array of extensions. Format: {"java", "xml"}
	 * @return an array of suffixes. Format: {".java", ".xml"}
	 */
	private static String[] toSuffixes(String[] extensions) {
		String[] suffixes = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			suffixes[i] = "." + extensions[i];
		}
		return suffixes;
	}

	/**
	 * Copies a file to a directory preserving the file date.
	 * <p>
	 * This method copies the contents of the specified source file
	 * to a file of the same name in the specified destination directory.
	 * The destination directory is created if it does not exist.
	 * If the destination file exists, then this method will overwrite it.
	 *
	 * @param srcFile  an existing file to copy, must not be <code>null</code>
	 * @param destDir  the directory to place the copy in, must not be <code>null</code>
	 *
	 * @throws NullPointerException if source or destination is null
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @see #copyFile(File, File, boolean)
	 */
	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
		copyFileToDirectory(srcFile, destDir, true);
	}

	/**
	 * Copies a file to a directory optionally preserving the file date.
	 * <p>
	 * This method copies the contents of the specified source file
	 * to a file of the same name in the specified destination directory.
	 * The destination directory is created if it does not exist.
	 * If the destination file exists, then this method will overwrite it.
	 *
	 * @param srcFile  an existing file to copy, must not be <code>null</code>
	 * @param destDir  the directory to place the copy in, must not be <code>null</code>
	 * @param preserveFileDate  true if the file date of the copy
	 *  should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @see #copyFile(File, File, boolean)
	 * @since Commons IO 1.3
	 */
	public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
	}

	/**
	 * Copies a file to a new location preserving the file date.
	 * <p>
	 * This method copies the contents of the specified source file to the
	 * specified destination file. The directory holding the destination file is
	 * created if it does not exist. If the destination file exists, then this
	 * method will overwrite it.
	 *
	 * @param srcFile  an existing file to copy, must not be <code>null</code>
	 * @param destFile  the new file, must not be <code>null</code>
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @see #copyFileToDirectory(File, File)
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	/**
	 * Copies a file to a new location.
	 * <p>
	 * This method copies the contents of the specified source file
	 * to the specified destination file.
	 * The directory holding the destination file is created if it does not exist.
	 * If the destination file exists, then this method will overwrite it.
	 *
	 * @param srcFile  an existing file to copy, must not be <code>null</code>
	 * @param destFile  the new file, must not be <code>null</code>
	 * @param preserveFileDate  true if the file date of the copy
	 *  should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @see #copyFileToDirectory(File, File, boolean)
	 */
	public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcFile.exists() == false) {
			throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
		}
		if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
			if (destFile.getParentFile().mkdirs() == false) {
				throw new IOException("Destination '" + destFile + "' directory cannot be created");
			}
		}
		if (destFile.exists() && destFile.canWrite() == false) {
			throw new IOException("Destination '" + destFile + "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

	/**
	 * Internal copy file method.
	 *
	 * @param srcFile  the validated source file, must not be <code>null</code>
	 * @param destFile  the validated destination file, must not be <code>null</code>
	 * @param preserveFileDate  whether to preserve the file date
	 * @throws IOException if an error occurs
	 */
	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		FileInputStream input = new FileInputStream(srcFile);
		try {
			FileOutputStream output = new FileOutputStream(destFile);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeSilently(output);
			}
		} finally {
			IOUtils.closeSilently(input);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Copies a directory to within another directory preserving the file dates.
	 * <p>
	 * This method copies the source directory and all its contents to a
	 * directory of the same name in the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 *
	 * @param srcDir  an existing directory to copy, must not be <code>null</code>
	 * @param destDir  the directory to place the copy in, must not be <code>null</code>
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @since Commons IO 1.2
	 */
	public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (srcDir.exists() && srcDir.isDirectory() == false) {
			throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
	}

	/**
	 * Copies a whole directory to a new location preserving the file dates.
	 * <p>
	 * This method copies the specified directory and all its child
	 * directories and files to the specified destination.
	 * The destination is the new location and name of the directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 *
	 * @param srcDir  an existing directory to copy, must not be <code>null</code>
	 * @param destDir  the new directory, must not be <code>null</code>
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @since Commons IO 1.1
	 */
	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		copyDirectory(srcDir, destDir, true);
	}

	/**
	 * Copies a whole directory to a new location.
	 * <p>
	 * This method copies the contents of the specified source directory
	 * to within the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 *
	 * @param srcDir  an existing directory to copy, must not be <code>null</code>
	 * @param destDir  the new directory, must not be <code>null</code>
	 * @param preserveFileDate  true if the file date of the copy
	 *  should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is <code>null</code>
	 * @throws IOException if source or destination is invalid
	 * @throws IOException if an IO error occurs during copying
	 * @since Commons IO 1.1
	 */
	public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcDir.exists() == false) {
			throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
		}
		if (srcDir.isDirectory() == false) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}
		doCopyDirectory(srcDir, destDir, preserveFileDate);
	}

	/**
	 * Internal copy directory method.
	 *
	 * @param srcDir  the validated source directory, must not be <code>null</code>
	 * @param destDir  the validated destination directory, must not be <code>null</code>
	 * @param preserveFileDate  whether to preserve the file date
	 * @throws IOException if an error occurs
	 * @since Commons IO 1.1
	 */
	private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}
		} else {
			if (destDir.mkdirs() == false) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
			if (preserveFileDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}
		if (destDir.canWrite() == false) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}
		// recurse
		File[] files = srcDir.listFiles();
		if (files == null) {  // null if security restricted
			throw new IOException("Failed to list contents of " + srcDir);
		}
		for (int i = 0; i < files.length; i++) {
			File copiedFile = new File(destDir, files[i].getName());
			if (files[i].isDirectory()) {
				doCopyDirectory(files[i], copiedFile, preserveFileDate);
			} else {
				doCopyFile(files[i], copiedFile, preserveFileDate);
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Copies bytes from the URL <code>source</code> to a file
	 * <code>destination</code>. The directories up to <code>destination</code>
	 * will be created if they don't already exist. <code>destination</code>
	 * will be overwritten if it already exists.
	 *
	 * @param source  the <code>URL</code> to copy bytes from, must not be <code>null</code>
	 * @param destination  the non-directory <code>File</code> to write bytes to
	 *  (possibly overwriting), must not be <code>null</code>
	 * @throws IOException if <code>source</code> URL cannot be opened
	 * @throws IOException if <code>destination</code> is a directory
	 * @throws IOException if <code>destination</code> cannot be written
	 * @throws IOException if <code>destination</code> needs creating but can't be
	 * @throws IOException if an IO error occurs during copying
	 */
	public static void copyURLToFile(URL source, File destination) throws IOException {
		InputStream input = source.openStream();
		try {
			FileOutputStream output = openOutputStream(destination);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeSilently(output);
			}
		} finally {
			IOUtils.closeSilently(input);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Recursively delete a directory.
	 *
	 * @param directory  directory to delete
	 * @throws IOException in case deletion is unsuccessful
	 */
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);
		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	/**
	 * Clean a directory without deleting it.
	 *
	 * @param directory directory to clean
	 * @throws IOException in case cleaning is unsuccessful
	 */
	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {  // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Reads the contents of a file into a String.
	 * The file is always closed.
	 *
	 * @param file  the file to read, must not be <code>null</code>
	 * @param encoding  the encoding to use, <code>null</code> means platform default
	 * @return the file contents, never <code>null</code>
	 * @throws IOException in case of an I/O error
	 * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
	 */
	public static String readFileToString(File file, String encoding) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			return IOUtils.toString(in, encoding);
		} finally {
			IOUtils.closeSilently(in);
		}
	}


	/**
	 * Reads the contents of a file into a String using the default encoding for the VM.
	 * The file is always closed.
	 *
	 * @param file  the file to read, must not be <code>null</code>
	 * @return the file contents, never <code>null</code>
	 * @throws IOException in case of an I/O error
	 * @since Commons IO 1.3.1
	 */
	public static String readFileToString(File file) throws IOException {
		return readFileToString(file, null);
	}

	/**
	 * Reads the contents of a file into a byte array.
	 * The file is always closed.
	 *
	 * @param file  the file to read, must not be <code>null</code>
	 * @return the file contents, never <code>null</code>
	 * @throws IOException in case of an I/O error
	 * @since Commons IO 1.1
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			return IOUtils.stream2Bytes(in);
		} finally {
			IOUtils.closeSilently(in);
		}
	}

	/**
	 * Writes a byte array to a file creating the file if it does not exist.
	 * <p>
	 * NOTE: As from v1.3, the parent directories of the file will be created
	 * if they do not exist.
	 *
	 * @param file  the file to write to
	 * @param data  the content to write to the file
	 * @throws IOException in case of an I/O error
	 * @since Commons IO 1.1
	 */
	public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			out.write(data);
		} finally {
			IOUtils.closeSilently(out);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Delete a file. If file is a directory, delete it and all sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>You get exceptions when a file or directory cannot be deleted.
	 *      (java.io.File methods returns a boolean)</li>
	 * </ul>
	 *
	 * @param file  file or directory to delete, must not be <code>null</code>
	 * @throws NullPointerException if the directory is <code>null</code>
	 * @throws IOException in case deletion is unsuccessful
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.exists()) {
				throw new FileNotFoundException("File does not exist: " + file);
			}
			if (!file.delete()) {
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	/**
	 * Schedule a file to be deleted when JVM exits.
	 * If file is directory delete it and all sub-directories.
	 *
	 * @param file  file or directory to delete, must not be <code>null</code>
	 * @throws NullPointerException if the file is <code>null</code>
	 * @throws IOException in case deletion is unsuccessful
	 */
	public static void forceDeleteOnExit(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectoryOnExit(file);
		} else {
			file.deleteOnExit();
		}
	}

	/**
	 * Recursively schedule directory for deletion on JVM exit.
	 *
	 * @param directory  directory to delete, must not be <code>null</code>
	 * @throws NullPointerException if the directory is <code>null</code>
	 * @throws IOException in case deletion is unsuccessful
	 */
	private static void deleteDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectoryOnExit(directory);
		directory.deleteOnExit();
	}

	/**
	 * Clean a directory without deleting it.
	 *
	 * @param directory  directory to clean, must not be <code>null</code>
	 * @throws NullPointerException if the directory is <code>null</code>
	 * @throws IOException in case cleaning is unsuccessful
	 */
	private static void cleanDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {  // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDeleteOnExit(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	/**
	 * Make a directory, including any necessary but nonexistent parent
	 * directories. If there already exists a file with specified name or
	 * the directory cannot be created then an exception is thrown.
	 *
	 * @param directory  directory to create, must not be <code>null</code>
	 * @throws NullPointerException if the directory is <code>null</code>
	 * @throws IOException if the directory cannot be created
	 */
	public static void forceMkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (directory.isFile()) {
				String message =
						"File "
								+ directory
								+ " exists and is "
								+ "not a directory. Unable to create directory.";
				throw new IOException(message);
			}
		} else {
			if (!directory.mkdirs()) {
				String message =
						"Unable to create directory " + directory;
				throw new IOException(message);
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Recursively count size of a directory (sum of the length of all files).
	 *
	 * @param directory  directory to inspect, must not be <code>null</code>
	 * @return size of directory in bytes, 0 if directory is security restricted
	 * @throws NullPointerException if the directory is <code>null</code>
	 */
	public static long sizeOfDirectory(File directory) {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		long size = 0;

		File[] files = directory.listFiles();
		if (files == null) {  // null if security restricted
			return 0L;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.isDirectory()) {
				size += sizeOfDirectory(file);
			} else {
				size += file.length();
			}
		}

		return size;
	}

	//-----------------------------------------------------------------------
	/**
	 * Computes the checksum of a file using the CRC32 checksum routine.
	 * The value of the checksum is returned.
	 *
	 * @param file  the file to checksum, must not be <code>null</code>
	 * @return the checksum value
	 * @throws NullPointerException if the file or checksum is <code>null</code>
	 * @throws IllegalArgumentException if the file is a directory
	 * @throws IOException if an IO error occurs reading the file
	 * @since Commons IO 1.3
	 */
	public static long checksumCRC32(File file) throws IOException {
		CRC32 crc = new CRC32();
		checksum(file, crc);
		return crc.getValue();
	}

	/**
	 * Computes the checksum of a file using the specified checksum object.
	 * Multiple files may be checked using one <code>Checksum</code> instance
	 * if desired simply by reusing the same checksum object.
	 * For example:
	 * <pre>
	 *   long csum = FileUtils.checksum(file, new CRC32()).getValue();
	 * </pre>
	 *
	 * @param file  the file to checksum, must not be <code>null</code>
	 * @param checksum  the checksum object to be used, must not be <code>null</code>
	 * @return the checksum specified, updated with the content of the file
	 * @throws NullPointerException if the file or checksum is <code>null</code>
	 * @throws IllegalArgumentException if the file is a directory
	 * @throws IOException if an IO error occurs reading the file
	 * @since Commons IO 1.3
	 */
	public static Checksum checksum(File file, Checksum checksum) throws IOException {
		if (file.isDirectory()) {
			throw new IllegalArgumentException("Checksums can't be computed on directories");
		}
		InputStream in = null;
		try {
			in = new CheckedInputStream(new FileInputStream(file), checksum);
			IOUtils.copy(in, new OutputStream() {
				@Override
				public void write(int b) throws IOException {

				}
			});
		} finally {
			IOUtils.closeSilently(in);
		}
		return checksum;
	}

	/**
	 * 获取assets文件夹下面的文件内容
	 *
	 * @param context  context
	 * @param filePath assets文件路径
	 * @return 文件内容
	 */
	public static String getAssetsFileContent(Context context, String filePath) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(filePath);
			return IOUtils.stream2String(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			IOUtils.closeSilently(inputStream);
		}
		return "";
	}

	/**
	 * 判断在assets目录中是否是文件夹
	 *
	 * @param context context
	 * @param path    文件路径
	 * @return boolean
	 */
	public static boolean isDirInAssets(Context context, String path) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(path);
			return false;
		}
		catch (IOException e) {
			return true;
		}
		finally {
			IOUtils.closeSilently(inputStream);
		}
	}

	@Deprecated
	public static byte[] getBytesFromFile(String filePath) {
		try {
			return readFileToByteArray(new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Deprecated
	public static boolean saveBytesToFile(byte[] data, String filePath) {
		try {
			writeByteArrayToFile(new File(filePath), data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Deprecated
	public static boolean isFileExisted(String filePath) {
		boolean result = false;
		File file = new File(filePath);
		if (file.exists()) {
			if (file.length() == 0) {
				file.delete();
			} else {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 获取文件的uri，Android N 以上版本 需要临时授权应用间共享文件
	 * @param context
	 * @param file
	 * @return
	 */
	public static Uri getUriForFile(Context context, File file) {
		HDRuntimeContext HDRuntimeContext = com.hotdog.hdlibrary.HDRuntimeContext.get();
		String packageName = HDRuntimeContext.getPackageName();
		if (HDRuntimeContext.isDebug()) {
			Log.e("******packageName******", packageName);
		}
		
		if (context == null || file == null) {
			return null;
		}
		Uri uri;
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context, packageName + ".fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}

}