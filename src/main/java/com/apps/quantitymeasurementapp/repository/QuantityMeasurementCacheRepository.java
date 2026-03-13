package com.apps.quantitymeasurementapp.repository;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.apps.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import com.apps.quantitymeasurementapp.exception.QuantityMeasurementException;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

	private final List<QuantityMeasurementEntity> cache = new ArrayList<>();
	private final Path storagePath;

	public QuantityMeasurementCacheRepository() {
		this(Path.of("quantity-measurement-history.ser"));
	}

	public QuantityMeasurementCacheRepository(Path storagePath) {
		this.storagePath = storagePath;
		loadHistory();
	}

	@Override
	public synchronized void saveMeasurement(QuantityMeasurementEntity entity) {
		cache.add(entity);
		writeEntity(entity);
	}

	@Override
	public synchronized List<QuantityMeasurementEntity> getAllMeasurements() {
		return new ArrayList<>(cache);
	}

	private void loadHistory() {

		if (!Files.exists(storagePath)) {
			return;
		}

		try {

			if (Files.size(storagePath) >= 2) {

				try (FileInputStream fis = new FileInputStream(storagePath.toFile())) {
					int b1 = fis.read();
					int b2 = fis.read();

					if (b1 != 0xAC || b2 != 0xED) {
						throw new IOException("Corrupted serialization header");
					}
				}

			} else {
				throw new IOException("File too small to be valid serialization");
			}

			try (ObjectInputStream inputStream =
					new ObjectInputStream(new FileInputStream(storagePath.toFile()))) {

				while (true) {
					Object record = inputStream.readObject();

					if (record instanceof QuantityMeasurementEntity) {
						cache.add((QuantityMeasurementEntity) record);
					} else {
						throw new IOException("Invalid object in history file");
					}
				}
			}

		} 
		catch (EOFException ignored) {
		} 
		catch (IOException | ClassNotFoundException exception) {
			recoverCorruptedHistory(exception);
		}
	}

	private void recoverCorruptedHistory(Exception cause) {

		Path corruptedBackup =
				storagePath.resolveSibling(storagePath.getFileName().toString() + ".corrupted");

		try {
			Files.move(storagePath, corruptedBackup, StandardCopyOption.REPLACE_EXISTING);
			cache.clear();
		} catch (IOException backupException) {
			backupException.addSuppressed(cause);
			throw new QuantityMeasurementException(
					"Failed to load measurement history", backupException);
		}
	}

	private void writeEntity(QuantityMeasurementEntity entity) {

		boolean append = Files.exists(storagePath) && storagePath.toFile().length() > 0;

		try (ObjectOutputStream outputStream =
				append
				? new AppendableObjectOutputStream(new FileOutputStream(storagePath.toFile(), true))
				: new ObjectOutputStream(new FileOutputStream(storagePath.toFile(), true))) {

			outputStream.writeObject(entity);

		} catch (IOException exception) {
			throw new QuantityMeasurementException(
					"Failed to save measurement history", exception);
		}
	}
}
