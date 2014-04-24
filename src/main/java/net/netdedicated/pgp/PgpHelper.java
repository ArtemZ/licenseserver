package net.netdedicated.pgp;

import com.google.common.io.Closeables;
import net.netdedicated.ConfigHolder;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.*;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/14/14
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PgpHelper {
	public static final int BUFFER_SIZE = 1 << 16;
	public static byte[] encode(byte[] message) throws IOException, PGPException {
		Security.addProvider(new BouncyCastleProvider());
		Boolean asciiArmor = true;
		File keyRingFile = new File(ConfigHolder.getConfig().getKeyRingUrl());
		if(!keyRingFile.exists()){
			//throw new PGPException("tets");
			System.out.println("File " + keyRingFile.getAbsolutePath() + " doesn't exist");
			System.exit(1);
		}
		/*File outputFile = new File(args[0] + ".bcs");*/
		/*outputFile.createNewFile();*/
		PGPSecretKey pgpSecretKey = loadSecretKey(keyRingFile);
		PGPPrivateKey pgpPrivateKey = pgpSecretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build("".toCharArray()));
		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSecretKey.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
		signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, pgpPrivateKey);

		@SuppressWarnings("unchecked")
		Iterator<String> it = pgpSecretKey.getPublicKey().getUserIDs();
		if (it.hasNext()) {
			PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
			spGen.setSignerUserID(false, it.next());
			signatureGenerator.setHashedSubpackets(spGen.generate());
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream outputStream = null;
		if (asciiArmor) {
			outputStream = new ArmoredOutputStream(baos);
		}
		else {
			outputStream = baos;
		}

		PGPCompressedDataGenerator  compressDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.UNCOMPRESSED);
		BCPGOutputStream bcOutputStream = new BCPGOutputStream(compressDataGenerator.open(outputStream));
		signatureGenerator.generateOnePassVersion(false).encode(bcOutputStream);

		PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
		/*OutputStream literalDataGenOutputStream = literalDataGenerator.open(bcOutputStream, PGPLiteralData.BINARY, fileToSign);*/
		OutputStream literalDataGenOutputStream = literalDataGenerator.open(bcOutputStream, PGPLiteralData.BINARY, "fake_name", message.length, new Date());

		try {
			literalDataGenOutputStream.write(message);
			signatureGenerator.update(message);

			literalDataGenerator.close();
			signatureGenerator.generate().encode(bcOutputStream);

		} catch (SignatureException se){
			throw new PGPException(se.getMessage());
		} finally {
			outputStream.close();
			baos.close();
			bcOutputStream.close();
			literalDataGenOutputStream.close();
			compressDataGenerator.close();
		}


		/*FileInputStream fis = new FileInputStream(fileToSign);

		int ch;
		while ((ch = fis.read()) >= 0) {
			literalDataGenOutputStream.write(ch);
			signatureGenerator.update((byte)ch);
		}

		literalDataGenerator.close();
		fis.close();

		signatureGenerator.generate().encode(bcOutputStream);
		compressDataGenerator.close();
		outputStream.close();*/
		/*outputStream.toString()*/
		return baos.toByteArray();
	}
	public static PGPSecretKey loadSecretKey(File fileUrl) throws PGPException, IOException {
		FileInputStream keyIn = new FileInputStream(fileUrl);
		PGPSecretKeyRingCollection pgpSecretKeyRingCollection = new PGPSecretKeyRingCollection(keyIn);
		PGPSecretKey key = null;
		Iterator rIt = pgpSecretKeyRingCollection.getKeyRings();

		while (key == null && rIt.hasNext()){
			PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
			Iterator kIt = kRing.getSecretKeys();
			while (key == null && kIt.hasNext()){
				PGPSecretKey k = (PGPSecretKey) kIt.next();
				if(k.isSigningKey()){ key = k; }
			}
		}
		if (key == null){
			throw new PGPException("No secret keys found in " + fileUrl);
		}
		return key;
	}
}
